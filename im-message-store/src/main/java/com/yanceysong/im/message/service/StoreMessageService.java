package com.yanceysong.im.message.service;

import com.yanceysong.im.common.model.content.MessageContent;
import com.yanceysong.im.common.model.content.GroupChatMessageContent;
import com.yanceysong.im.message.dao.ImGroupMessageHistoryEntity;
import com.yanceysong.im.message.dao.ImMessageBodyEntity;
import com.yanceysong.im.message.dao.ImMessageHistoryEntity;
import com.yanceysong.im.message.dao.mapper.ImGroupMessageHistoryMapper;
import com.yanceysong.im.message.dao.mapper.ImMessageBodyMapper;
import com.yanceysong.im.message.dao.mapper.ImMessageHistoryMapper;
import com.yanceysong.im.message.model.DoStoreGroupMessageDto;
import com.yanceysong.im.message.model.DoStoreP2PMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName StoreMessageService
 * @Description MQ 异步持久化实类
 * @date 2023/5/16 11:08
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
@Service
public class StoreMessageService {

    @Resource
    private ImMessageBodyMapper messageBodyMapper;

    @Resource
    private ImMessageHistoryMapper messageHistoryMapper;

    @Resource
    private ImGroupMessageHistoryMapper groupMessageHistoryMapper;

    /**
     * MQ异步 单聊消息落库持久化
     * 当出现异常，必须强制回滚: 简单回滚策略（失败就回滚）
     * TODO 回滚策略待升级, 重试三次：
     * 第一次 10s，第二次 20s，第三次 40s。
     * 还是不行就报紧急日志
     *
     * @param doStoreP2PMessageDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void doStoreP2PMessage(DoStoreP2PMessageDto doStoreP2PMessageDto) {
        try {
            messageBodyMapper.insert(doStoreP2PMessageDto.getImMessageBodyEntity());
            List<ImMessageHistoryEntity> imMessageHistoryEntities =
                    extractToP2PMessageHistory(
                            doStoreP2PMessageDto.getMessageContent(),
                            doStoreP2PMessageDto.getImMessageBodyEntity());
            messageHistoryMapper.insertBatchSomeColumn(imMessageHistoryEntities);
        } catch (Exception e) {
            log.error("单聊消息持久化失败 {}", e.getMessage());
        }
    }

    /**
     * MQ异步 群聊消息落库持久化
     * 当出现异常，必须强制回滚: 简单回滚策略（失败就回滚）
     * TODO 回滚策略待升级, 重试三次：
     * 第一次 10s，第二次 20s，第三次 40s。
     * 还是不行就报紧急日志
     *
     * @param doStoreGroupMessageDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void doStoreGroupMessage(DoStoreGroupMessageDto doStoreGroupMessageDto) {
        try {
            messageBodyMapper.insert(doStoreGroupMessageDto.getImMessageBodyEntity());
            ImGroupMessageHistoryEntity imGroupMessageHistoryEntity =
                    extractToGroupMessageHistory(doStoreGroupMessageDto.getGroupChatMessageContent(), doStoreGroupMessageDto.getImMessageBodyEntity());
            groupMessageHistoryMapper.insert(imGroupMessageHistoryEntity);
        } catch (Exception e) {
            log.error("群聊消息持久化失败 {}", e.getMessage());
        }
    }

    /**
     * 【写扩散】单聊历史记录存储实体类，双方消息冗余备份，并纪录消息拥有者 ownId
     *
     * @param messageContent
     * @param imMessageBodyEntity
     * @return
     */
    private List<ImMessageHistoryEntity> extractToP2PMessageHistory(
            MessageContent messageContent, ImMessageBodyEntity imMessageBodyEntity) {
        List<ImMessageHistoryEntity> list = new ArrayList<>();
        // 己方历史消息记录表 DAO 实体类
        ImMessageHistoryEntity fromMsgHistory = getMsgHistory(messageContent.getFromId(), messageContent, imMessageBodyEntity);
        // 对方历史消息记录表 DAO 实体类
        ImMessageHistoryEntity toMsgHistory = getMsgHistory(messageContent.getToId(), messageContent, imMessageBodyEntity);
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
    private ImGroupMessageHistoryEntity extractToGroupMessageHistory(GroupChatMessageContent messageContent, ImMessageBodyEntity imMessageBodyEntity) {
        ImGroupMessageHistoryEntity imGroupMessageHistoryEntity = new ImGroupMessageHistoryEntity();
        imGroupMessageHistoryEntity.setAppId(messageContent.getAppId());
        imGroupMessageHistoryEntity.setFromId(messageContent.getFromId());
        imGroupMessageHistoryEntity.setGroupId(messageContent.getGroupId());
        imGroupMessageHistoryEntity.setMessageTime(messageContent.getMessageTime());

        imGroupMessageHistoryEntity.setMessageKey(imMessageBodyEntity.getMessageKey());
        imGroupMessageHistoryEntity.setMessageTime(imMessageBodyEntity.getMessageTime());
        imGroupMessageHistoryEntity.setCreateTime(System.currentTimeMillis());

        return imGroupMessageHistoryEntity;
    }

    private ImMessageHistoryEntity getMsgHistory(String userId, MessageContent msgContent, ImMessageBodyEntity msgBody) {
        ImMessageHistoryEntity msgHistory = new ImMessageHistoryEntity();
        msgHistory.setAppId(msgContent.getAppId());
        msgHistory.setFromId(msgContent.getFromId());
        msgHistory.setToId(msgContent.getToId());
        msgHistory.setMessageTime(msgContent.getMessageTime());
        // 设置消息拥有者
        msgHistory.setOwnerId(userId);
        msgHistory.setMessageKey(msgBody.getMessageKey());
        msgHistory.setCreateTime(System.currentTimeMillis());
        return msgHistory;
    }

}

