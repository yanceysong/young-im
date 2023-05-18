package com.yanceysong.im.domain.message.service.sync;

import com.yanceysong.im.codec.pack.message.MessageReadPack;
import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.enums.command.MessageCommand;
import com.yanceysong.im.common.model.content.MessageReceiveAckContent;
import com.yanceysong.im.common.model.read.MessageReadContent;
import com.yanceysong.im.domain.conversation.service.ConversationService;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName MessageSyncServiceImpl
 * @Description 消息同步服务类 用于处理消息接收确认，同步等操作
 * @date 2023/5/17 13:50
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class MessageSyncServiceImpl implements MessageSyncService {

    @Resource
    private MessageProducer messageProducer;

    @Resource
    private ConversationService conversationServiceImpl;

    @Override
    public void receiveMark(MessageReceiveAckContent pack) {
        // 确认接收 ACK 发送给在线目标用户全端
        messageProducer.sendToUserAllClient(pack.getToId(),
                MessageCommand.MSG_RECEIVE_ACK, pack, pack.getAppId());
    }

    @Override
    public void readMark(MessageReadContent messageContent, Command notify, Command receipt) {
        conversationServiceImpl.messageMarkRead(messageContent);
        MessageReadPack messageReadPack = Content2Pack(messageContent);
        syncToSender(messageReadPack, messageContent, notify);
        // 防止自己给自己发送消息
        if (!messageContent.getFromId().equals(messageContent.getToId())) {
            // 发送给对方
            messageProducer.sendToUserAllClient(
                    messageContent.getToId(),
                    receipt, messageReadPack,
                    messageContent.getAppId()
            );
        }
    }

    private void syncToSender(MessageReadPack pack, MessageReadContent content, Command command) {
        messageProducer.sendToUserExceptClient(content.getFromId(), command, pack, content);
    }

    private MessageReadPack Content2Pack(MessageReadContent messageContent) {
        MessageReadPack messageReadPack = new MessageReadPack();
        messageReadPack.setMessageSequence(messageContent.getMessageSequence());
        messageReadPack.setFromId(messageContent.getFromId());
        messageReadPack.setToId(messageContent.getToId());
        messageReadPack.setGroupId(messageContent.getGroupId());
        messageReadPack.setConversationType(messageContent.getConversationType());
        return messageReadPack;
    }

}
