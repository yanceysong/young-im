package com.yanceysong.im.domain.message.service;

import com.yanceysong.im.codec.pack.ChatMessageAck;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.SeqConstants;
import com.yanceysong.im.common.constant.ThreadPoolConstants;
import com.yanceysong.im.common.enums.command.GroupEventCommand;
import com.yanceysong.im.common.model.content.GroupChatMessageContent;
import com.yanceysong.im.common.model.content.MessageContent;
import com.yanceysong.im.common.model.content.OfflineMessageContent;
import com.yanceysong.im.common.thradPool.ThreadPoolFactory;
import com.yanceysong.im.domain.group.model.req.group.SendGroupMessageReq;
import com.yanceysong.im.domain.group.service.ImGroupMemberService;
import com.yanceysong.im.domain.message.model.resp.SendMessageResp;
import com.yanceysong.im.domain.message.seq.RedisSequence;
import com.yanceysong.im.domain.message.service.check.CheckSendMessageImpl;
import com.yanceysong.im.domain.message.service.store.MessageStoreServiceImpl;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName GroupMessageService
 * @Description 群聊逻辑
 * @date 2023/5/16 10:44
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
@Service
public class GroupMessageService {

    @Resource
    private CheckSendMessageImpl checkSendMessageImpl;
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private MessageStoreServiceImpl messageStoreServiceImpl;
    @Resource
    private RedisSequence redisSequence;
    @Resource
    private ImGroupMemberService imGroupMemberServiceImpl;
    @Resource
    private RedisTemplate<String, String> stringRedisTemplate;

    public void processor(GroupChatMessageContent messageContent) {
        // 日志打印
        log.info("消息 ID [{}] 开始处理", messageContent.getMessageId());
        GroupChatMessageContent messageCache = messageStoreServiceImpl.getMessageCacheByMessageId(messageContent.getAppId(), messageContent.getMessageId(), GroupChatMessageContent.class);
        if (messageCache != null) {
            ThreadPoolFactory.getThreadPool(ThreadPoolConstants.GROUP_MESSAGE_SERVICE, true).submit(() -> {
                // 线程池执行消息同步，发送，回应等任务流程
                doThreadPoolTask(messageContent);
            });
            return;
        }
        // 定义群聊消息的 Sequence, 客户端根据 seq 进行排序
        // key: appId + Seq + (from + toId) / groupId
        long seq = redisSequence.doGetSeq(messageContent.getAppId()
                + SeqConstants.GROUP_MESSAGE_SEQ
                + messageContent.getGroupId());
        messageContent.setMessageSequence(seq);

        /*
         * 线程池优化单聊消息处理逻辑
         */
        ThreadPoolFactory.getThreadPool(ThreadPoolConstants.GROUP_MESSAGE_SERVICE, true)
                .submit(() -> {
                    // 1. 消息持久化落库
                    messageStoreServiceImpl.storeGroupMessage(messageContent);

                    // 查询群组所有成员进行消息分发
                    List<String> groupMemberIds = imGroupMemberServiceImpl
                            .getGroupMemberId(messageContent.getGroupId(), messageContent.getAppId());

                    messageContent.setMemberIds(groupMemberIds);

                    // 2.在异步持久化之后执行离线消息存储
                    OfflineMessageContent offlineMessage = getOfflineMessage(messageContent);
                    offlineMessage.setToId(messageContent.getGroupId());
                    messageStoreServiceImpl.storeGroupOfflineMessage(offlineMessage, groupMemberIds);

                    // 线程池执行消息同步，发送，回应等任务流程
                    doThreadPoolTask(messageContent);

                    // 消息缓存
                    messageStoreServiceImpl.setMessageCacheByMessageId(
                            messageContent.getAppId(), messageContent.getMessageId(), messageContent);
                });
        log.info("消息 ID [{}] 处理完成", messageContent.getMessageId());
    }

    private OfflineMessageContent getOfflineMessage(GroupChatMessageContent messageContent) {
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
     * 线程池执行任务
     *
     * @param messageContent 消息上下文
     */
    private void doThreadPoolTask(GroupChatMessageContent messageContent) {
        // 2. 返回应答报文 ACK 给自己
        ack(messageContent, ResponseVO.successResponse());
        // 3. 发送消息，同步发送方多端设备
        syncToSender(messageContent);
        // 4. 发送消息给对方所有在线端(TODO 离线端也要做消息同步)
        dispatchMessage(messageContent);
    }

    public SendMessageResp send(SendGroupMessageReq req) {
        SendMessageResp sendMessageResp = new SendMessageResp();
        GroupChatMessageContent message = new GroupChatMessageContent();
        message.setAppId(req.getAppId());
        message.setClientType(req.getClientType());
        message.setImei(req.getImei());
        message.setMessageId(req.getMessageId());
        message.setFromId(req.getFromId());
        message.setMessageBody(req.getMessageBody());
        message.setMessageTime(req.getMessageTime());
        message.setGroupId(req.getGroupId());

        messageStoreServiceImpl.storeGroupMessage(message);

        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());
        //2.发消息给同步在线端
        syncToSender(message);
        //3.发消息给对方在线端
        dispatchMessage(message);
        return sendMessageResp;
    }

    /**
     * 前置校验
     * 1. 这个用户是否被禁言 是否被禁用
     * 2. 发送方是否在群组内
     *
     * @param fromId
     * @param groupId
     * @param appId
     * @return
     */
    public ResponseVO serverPermissionCheck(String fromId, String groupId, Integer appId) {
        return checkSendMessageImpl.checkGroupMessage(fromId, groupId, appId);
    }

    /**
     * ACK 应答报文包装和发送
     *
     * @param messageContent
     * @param responseVO
     */
    protected void ack(MessageContent messageContent, ResponseVO responseVO) {
        log.info("[GROUP] msg ack, msgId = {}, checkResult = {}", messageContent.getMessageId(), responseVO.getCode());

        // ack 包塞入消息 id，告知客户端端 该条消息已被成功接收
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        responseVO.setData(chatMessageAck);
        // 发送消息，回传给发送方端
        messageProducer.sendToUserOneClient(messageContent.getFromId(),
                GroupEventCommand.GROUP_MSG_ACK, responseVO, messageContent);
    }

    /**
     * 消息同步【发送方除本端所有端消息同步】
     *
     * @param messageContent
     */
    protected void syncToSender(MessageContent messageContent) {
        log.info("[GROUP] 发送方消息同步");
        messageProducer.sendToUserExceptClient(messageContent.getFromId(),
                GroupEventCommand.MSG_GROUP, messageContent, messageContent);
    }

    /**
     * [群聊] 消息发送【接收端所有端都需要接收消息】
     *
     * @param messageContent
     * @return
     */
    protected void dispatchMessage(GroupChatMessageContent messageContent) {
        messageContent.getMemberIds().stream()
                // 排除自己
                .filter(memberId -> !memberId.equals(messageContent.getFromId()))
                .forEach(memberId -> messageProducer.sendToUserAllClient(
                        memberId, GroupEventCommand.MSG_GROUP,
                        messageContent, messageContent.getAppId()));
    }

}
