package com.yanceysong.im.domain.message.service;

import com.yanceysong.im.codec.pack.ChatMessageAck;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.ThreadPoolConstants;
import com.yanceysong.im.common.enums.command.GroupEventCommand;
import com.yanceysong.im.common.model.GroupChatMessageContent;
import com.yanceysong.im.common.model.MessageContent;
import com.yanceysong.im.common.thradPool.ThreadPoolFactory;
import com.yanceysong.im.domain.group.model.req.group.SendGroupMessageReq;
import com.yanceysong.im.domain.message.model.resp.SendMessageResp;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    private CheckSendMessageService checkSendMessageService;
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private MessageStoreService messageStoreService;

    public void processor(GroupChatMessageContent messageContent) {
        // 日志打印
        log.info("消息 ID [{}] 开始处理", messageContent.getMessageId());
        /*
         * 线程池优化单聊消息处理逻辑
         */
        ThreadPoolFactory.getThreadPool(ThreadPoolConstants.GROUP_MESSAGE_SERVICE, true)
                .submit(() -> {
                    // 消息持久化落库
                    messageStoreService.storeGroupMessage(messageContent);
                    // 2. 返回应答报文 ACK 给自己
                    ack(messageContent, ResponseVO.successResponse());
                    // 3. 发送消息，同步发送方多端设备
                    syncToSender(messageContent);
                    // 4. 发送消息给对方所有在线端(TODO 离线端也要做消息同步)
                    dispatchMessage(messageContent);
                });
        log.info("消息 ID [{}] 处理完成", messageContent.getMessageId());
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

        messageStoreService.storeGroupMessage(message);

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
    protected ResponseVO serverPermissionCheck(String fromId, String groupId, Integer appId) {
        return checkSendMessageService.checkGroupMessage(fromId, groupId, appId);
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
        messageContent.getMemberId().stream()
                // 排除自己
                .filter(memberId -> !memberId.equals(messageContent.getFromId()))
                .forEach(memberId -> messageProducer.sendToUserAllClient(
                        memberId, GroupEventCommand.MSG_GROUP,
                        messageContent, messageContent.getAppId()));
    }

}
