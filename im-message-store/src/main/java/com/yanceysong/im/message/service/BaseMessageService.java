package com.yanceysong.im.message.service;

import com.yanceysong.im.common.model.content.GroupChatMessageContent;
import com.yanceysong.im.common.model.content.MessageContent;
import com.yanceysong.im.message.dao.ImGroupMessageHistoryEntity;
import com.yanceysong.im.message.dao.ImMessageBodyEntity;
import com.yanceysong.im.message.dao.ImMessageHistoryEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName BaseMessageService
 * @Description
 * @date 2023/7/3 11:52
 * @Author yanceysong
 * @Version 1.0
 */
public class BaseMessageService {
    /**
     * 【写扩散】单聊历史记录存储实体类，双方消息冗余备份，并纪录消息拥有者 ownId
     *
     * @param messageContent
     * @param imMessageBodyEntity
     * @return
     */
    protected List<ImMessageHistoryEntity> extractToP2PMessageHistory(
            MessageContent messageContent, ImMessageBodyEntity imMessageBodyEntity) {
        List<ImMessageHistoryEntity> list = new ArrayList<>();
        // 己方历史消息记录表 DAO 实体类
        ImMessageHistoryEntity fromMsgHistory = getMsgHistory(messageContent.getSendId(), messageContent, imMessageBodyEntity);
        // 对方历史消息记录表 DAO 实体类
        ImMessageHistoryEntity toMsgHistory = getMsgHistory(messageContent.getReceiverId(), messageContent, imMessageBodyEntity);
        list.add(fromMsgHistory);
        list.add(toMsgHistory);
        return list;
    }

    /**
     * 【读扩散】群聊历史记录存储实体类
     *
     * @param messageContent
     * @param imMessageBodyEntity
     * @return
     */
    protected ImGroupMessageHistoryEntity extractToGroupMessageHistory(GroupChatMessageContent messageContent, ImMessageBodyEntity imMessageBodyEntity) {
        ImGroupMessageHistoryEntity imGroupMessageHistoryEntity = new ImGroupMessageHistoryEntity();
        imGroupMessageHistoryEntity.setAppId(messageContent.getAppId());
        imGroupMessageHistoryEntity.setSendId(messageContent.getSendId());
        imGroupMessageHistoryEntity.setGroupId(messageContent.getGroupId());
        imGroupMessageHistoryEntity.setMessageTime(messageContent.getMessageTime());
        imGroupMessageHistoryEntity.setSequence(messageContent.getMessageSequence());
        imGroupMessageHistoryEntity.setMessageKey(imMessageBodyEntity.getMessageKey());
        imGroupMessageHistoryEntity.setMessageTime(imMessageBodyEntity.getMessageTime());
        imGroupMessageHistoryEntity.setCreateTime(System.currentTimeMillis());
        return imGroupMessageHistoryEntity;
    }

    protected ImMessageHistoryEntity getMsgHistory(String userId, MessageContent msgContent, ImMessageBodyEntity msgBody) {
        ImMessageHistoryEntity msgHistory = new ImMessageHistoryEntity();
        msgHistory.setAppId(msgContent.getAppId());
        msgHistory.setSendId(msgContent.getSendId());
        msgHistory.setReceiverId(msgContent.getReceiverId());
        msgHistory.setMessageTime(msgContent.getMessageTime());
        // 设置消息拥有者
        msgHistory.setOwnerId(userId);
        msgHistory.setMessageKey(msgBody.getMessageKey());
        msgHistory.setCreateTime(System.currentTimeMillis());
        return msgHistory;
    }
}
