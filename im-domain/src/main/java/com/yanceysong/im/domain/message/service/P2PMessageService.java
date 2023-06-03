package com.yanceysong.im.domain.message.service;

import com.alibaba.fastjson2.JSON;
import com.yanceysong.im.codec.pack.ChatMessageAck;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.SeqConstants;
import com.yanceysong.im.common.constant.ThreadPoolConstants;
import com.yanceysong.im.common.enums.command.MessageCommand;
import com.yanceysong.im.common.enums.error.MessageErrorCode;
import com.yanceysong.im.common.model.ClientInfo;
import com.yanceysong.im.common.model.content.MessageContent;
import com.yanceysong.im.common.model.content.MessageReceiveAckContent;
import com.yanceysong.im.common.model.content.OfflineMessageContent;
import com.yanceysong.im.common.thradPool.ThreadPoolFactory;
import com.yanceysong.im.domain.message.model.req.SendMessageReq;
import com.yanceysong.im.domain.message.model.resp.SendMessageResp;
import com.yanceysong.im.domain.message.seq.RedisSequence;
import com.yanceysong.im.domain.message.service.check.CheckSendMessageImpl;
import com.yanceysong.im.domain.message.service.store.MessageStoreServiceImpl;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import com.yanceysong.im.infrastructure.supports.ids.ConversationIdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName P2PMessageService
 * @Description 单聊逻辑
 * @date 2023/5/16 10:45
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
@Service
public class P2PMessageService {

    @Resource
    private CheckSendMessageImpl checkSendMessageServiceImpl;
    @Resource
    private MessageStoreServiceImpl messageStoreImpl;
    @Resource
    private CheckSendMessageImpl checkSendMessageImpl;
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private MessageStoreServiceImpl messageStoreServiceImpl;
    @Resource
    private RedisSequence redisSequence;

    public void processor(MessageContent messageContent) {
        // 日志打印
        log.info("消息 ID [{}] 开始处理", messageContent.getMessageId());
        // 设置临时缓存，避免消息无限制重发，当缓存失效，直接重新构建新消息进行处理
        String messageCacheByMessageId = messageStoreServiceImpl.getMessageCacheByMessageId(messageContent.getAppId(), messageContent.getMessageId());
        if (messageCacheByMessageId != null &&
                messageCacheByMessageId.equals(MessageErrorCode.MESSAGE_CACHE_EXPIRE.getError())) {
            // 说明缓存过期，服务端向客户端发送 ack 要求客户端重新生成 messageId
//            ack(messageContent, ResponseVO.errorResponse(MessageErrorCode.MESSAGE_CACHE_EXPIRE));
            // 不做处理。直到客户端计时器超时, 重投次数 超过了 最大重投次数
            // 客户端 本地，重新生成 messageId
            return;
        }
        //缓存没有过期，但是客户端还是重发了，幂等性
        MessageContent messageCache = JSON.parseObject(messageCacheByMessageId, MessageContent.class);
        if (messageCache != null) {
            ThreadPoolFactory.getThreadPool(ThreadPoolConstants.P2P_MESSAGE_SERVICE, true).execute(() -> {
                // 线程池执行消息同步，发送，回应等任务流程
                doThreadPoolTask(messageContent);
            });
            return;
        }

        // TODO 外提 Seq 存储，因为 seq 生成策略可以是 redis，也可以是一个新的服务专门处理。
        //  为了保证安全性需要对第三方接口进行异常捕获，因此不要将这段逻辑脏污线程池的逻辑，保证线程池的流式纯粹
        // 定义单聊消息的 Sequence, 客户端根据 seq 进行排序
        // key: appId + Seq + (from + toId) / groupId
        long seq = redisSequence.doGetSeq(messageContent.getAppId()
                + SeqConstants.MESSAGE_SEQ
                + ConversationIdGenerate.generateP2PId(messageContent.getFromId(),
                messageContent.getToId()));

        messageContent.setMessageSequence(seq);

        /*
         * 线程池优化单聊消息处理逻辑
         */
        ThreadPoolFactory.getThreadPool(ThreadPoolConstants.P2P_MESSAGE_SERVICE, true)
                .submit(() -> {
                    // 1. 消息持久化落库(MQ 异步)
                    messageStoreImpl.storeP2PMessage(messageContent);
                    // 2. 在异步持久化之后执行离线消息存储
                    OfflineMessageContent offlineMessage = getOfflineMessage(messageContent);
                    messageStoreImpl.storeOfflineMessage(offlineMessage);
                    // 线程池执行消息同步，发送，回应等任务流程
                    doThreadPoolTask(messageContent);
                    // 缓存消息
                    messageStoreImpl.setMessageCacheByMessageId(
                            messageContent.getAppId(), messageContent.getMessageId(), messageContent);
                });
        log.info("消息 ID [{}] 处理完成", messageContent.getMessageId());
    }

    private OfflineMessageContent getOfflineMessage(MessageContent messageContent) {
        OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
        offlineMessageContent.setAppId(messageContent.getAppId());
        offlineMessageContent.setMessageKey(messageContent.getMessageKey());
        offlineMessageContent.setMessageBody(messageContent.getMessageBody());
        offlineMessageContent.setMessageTime(messageContent.getMessageTime());
        offlineMessageContent.setExtra(messageContent.getExtra());
        offlineMessageContent.setFromId(messageContent.getFromId());
        offlineMessageContent.setToId(messageContent.getToId());
        offlineMessageContent.setMessageSequence(messageContent.getMessageSequence());
        return offlineMessageContent;
    }

    /**
     * 线程池执行消息同步，发送，回应等任务流程
     *
     * @param messageContent 消息上下文
     */
    private void doThreadPoolTask(MessageContent messageContent) {
        // 2. 返回应答报文 ACK 给自己
        ack(messageContent, ResponseVO.successResponse());
        // 3. 发送消息，同步发送方多端设备
        syncToSender(messageContent);
        // 4. 发送消息给对方所有在线端(TODO 离线端也要做消息同步)
        List<ClientInfo> clientInfos = dispatchMessage(messageContent);
        // 决策前移，因为离线用户无法走消息接收逻辑，也就无法识别命令
        // 这里将服务端接收确认迁移于此，保证离线用户也能实现消息可靠性
        if (clientInfos.isEmpty()) {
            // 如果接收方为空，代表目标用户离线，服务端代发响应 ACK 数据包
            receiveAckByServer(messageContent);
        }
    }

    /**
     * 服务端代替离线目标用户发送接受确认 ACK
     *
     * @param messageContent 消息上下文
     */
    public void receiveAckByServer(MessageContent messageContent) {
        MessageReceiveAckContent pack = new MessageReceiveAckContent();
        pack.setAppId(messageContent.getAppId());
        pack.setClientType(messageContent.getClientType());
        pack.setImei(messageContent.getImei());
        pack.setMessageKey(messageContent.getMessageKey());
        pack.setFromId(messageContent.getFromId());
        pack.setToId(messageContent.getToId());
        // 服务端发送接收确认 ACK 数据包
        pack.setServerSend(true);
        // 确认接收 ACK 发送给发送方指定端
        messageProducer.sendToUserOneClient(pack.getFromId(), MessageCommand.MSG_RECEIVE_ACK,
                pack, new ClientInfo(pack.getAppId(), pack.getClientType(), pack.getImei()));
    }

    /**
     * 管理员后台发送消息
     *
     * @param req 请求
     * @return 结果
     */
    public SendMessageResp send(SendMessageReq req) {

        SendMessageResp sendMessageResp = new SendMessageResp();
        MessageContent message = new MessageContent();
        message.setAppId(req.getAppId());
        message.setClientType(req.getClientType());
        message.setImei(req.getImei());
        message.setMessageId(req.getMessageId());
        message.setFromId(req.getFromId());
        message.setToId(req.getToId());
        message.setMessageBody(req.getMessageBody());
        message.setMessageTime(req.getMessageTime());

        //插入数据
        messageStoreServiceImpl.storeP2PMessage(message);
        sendMessageResp.setMessageId(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());

        //2.发消息给同步在线端
        syncToSender(message);
        //3.发消息给对方在线端
        dispatchMessage(message);
        return sendMessageResp;
    }

    /**
     * 前置校验（网关层rpc调用）
     * 1. 这个用户是否被禁言 是否被禁用
     * 2. 发送方和接收方是否是好友
     *
     * @param fromId 发送发id
     * @param toId   目标id
     * @param appId  appid
     * @return 结果¬
     */
    public ResponseVO<ResponseVO.NoDataReturn> serverPermissionCheck(String fromId, String toId, Integer appId) {
        ResponseVO<ResponseVO.NoDataReturn> responseVO = checkSendMessageImpl.checkSenderForbidAndMute(fromId, appId);
        if (!responseVO.isOk()) {
            return responseVO;
        }
        responseVO = checkSendMessageImpl.checkFriendShip(fromId, toId, appId);
        return responseVO;
    }

    /**
     * ACK 应答报文包装和发送 应答给发送方
     *
     * @param messageContent 消息上下文
     * @param responseVO 返回的实体类
     */
    public void ack(MessageContent messageContent, ResponseVO responseVO) {
        log.info("[P2P] msg ack, msgId = {}, checkResult = {}", messageContent.getMessageId(), responseVO.getCode());
        // ack 包塞入消息 id，告知客户端端 该条消息已被成功接收
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId(), messageContent.getMessageSequence());
        responseVO.setData(chatMessageAck);
        // 发送消息，回传给发送方端
        messageProducer.sendToUserOneClient(messageContent.getFromId(),
                MessageCommand.MSG_ACK, responseVO, messageContent);
    }

    /**
     * 消息同步【发送方除本端所有端消息同步】
     *
     * @param messageContent 消息上下文
     */
    public void syncToSender(MessageContent messageContent) {
        log.info("[P2P] 发送方消息同步");
        messageProducer.sendToUserExceptClient(
                messageContent.getFromId(),
                MessageCommand.MSG_P2P,
                messageContent, messageContent
        );
    }

    /**
     * [单聊] 消息发送【接收端所有端都需要接收消息】
     *
     * @param messageContent 消息上下文
     * @return 成功发送的clientInfo
     */
    public List<ClientInfo> dispatchMessage(MessageContent messageContent) {
        return messageProducer.sendToUserAllClient(
                messageContent.getToId(),
                MessageCommand.MSG_P2P,
                messageContent,
                messageContent.getAppId()
        );
    }

}

