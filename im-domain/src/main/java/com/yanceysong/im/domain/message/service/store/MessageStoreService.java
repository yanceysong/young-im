package com.yanceysong.im.domain.message.service.store;

import com.yanceysong.im.common.model.content.GroupChatMessageContent;
import com.yanceysong.im.common.model.content.MessageContent;
import com.yanceysong.im.common.model.content.OfflineMessageContent;

import java.util.List;

/**
 * @ClassName MessageStoreService
 * @Description
 * @date 2023/5/17 13:49
 * @Author yanceysong
 * @Version 1.0
 */
public interface MessageStoreService {

    /**
     * 单聊消息持久化(MQ 异步持久化)
     * @param messageContent
     */
    void storeP2PMessage(MessageContent messageContent);

    /**
     * 群聊消息持久化(MQ 异步持久化)
     * @param messageContent
     */
    void storeGroupMessage(GroupChatMessageContent messageContent);

    /**
     * 通过 MessageId 设置消息缓存
     * @param appId
     * @param messageId
     * @param messageContent
     */
    void setMessageCacheByMessageId(Integer appId, String messageId, Object messageContent);

    /**
     * 通过 MessageId 获取消息缓存
     * @param appId
     * @param messageId
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getMessageCacheByMessageId(Integer appId, String messageId, Class<T> clazz);

    /**
     * 【读扩散】存储单聊离线消息
     * @param messageContent
     */
    void storeOfflineMessage(OfflineMessageContent messageContent);

    /**
     * 【读扩散】存储群聊离线消息
     * @param messageContent
     * @param memberIds
     */
    void storeGroupOfflineMessage(OfflineMessageContent messageContent, List<String> memberIds);

}
