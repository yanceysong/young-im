package com.yanceysong.im.domain.message.service.store;

import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.common.constant.RabbitmqConstants;
import com.yanceysong.im.common.constant.RedisConstants;
import com.yanceysong.im.common.enums.conversation.ConversationTypeEnum;
import com.yanceysong.im.common.enums.error.MessageErrorCode;
import com.yanceysong.im.common.enums.friend.DelFlagEnum;
import com.yanceysong.im.common.model.content.GroupChatMessageContent;
import com.yanceysong.im.common.model.content.MessageBody;
import com.yanceysong.im.common.model.content.MessageContent;
import com.yanceysong.im.common.model.content.OfflineMessageContent;
import com.yanceysong.im.common.model.store.DoStoreGroupMessageDto;
import com.yanceysong.im.common.model.store.DoStoreP2PMessageDto;
import com.yanceysong.im.domain.conversation.service.ConversationServiceImpl;
import com.yanceysong.im.domain.message.dao.ImGroupMessageHistoryEntity;
import com.yanceysong.im.domain.message.dao.ImMessageBodyEntity;
import com.yanceysong.im.infrastructure.config.AppConfig;
import com.yanceysong.im.infrastructure.supports.ids.SnowflakeIdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName MessageStoreService
 * @Description 消息(MQ 异步)落库持久化
 * @date 2023/5/16 10:45
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class MessageStoreServiceImpl implements MessageStoreService {
    @Resource
    private ConversationServiceImpl conversationServiceImpl;

    @Resource
    private AppConfig appConfig;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 单聊消息持久化(MQ 异步持久化)
     *
     * @param messageContent
     */
    @Override
    public void storeP2PMessage(MessageContent messageContent) {
        // 将 MessageContent 转换成 MessageBody
        MessageBody messageBody = extractMessageBody(messageContent);
        DoStoreP2PMessageDto dto = new DoStoreP2PMessageDto();
        messageContent.setMessageKey(messageBody.getMessageKey());
        dto.setMessageContent(messageContent);
        dto.setMessageBody(messageBody);
        // MQ 异步持久化, 将实体消息传递给 MQ
        rabbitTemplate.convertAndSend(
                RabbitmqConstants.STORE_P2P_MESSAGE, "",
                JSONObject.toJSONString(dto));
    }

    /**
     * 群聊消息持久化
     *
     * @param messageContent
     * @return
     */
    @Override
    public void storeGroupMessage(GroupChatMessageContent messageContent) {
        MessageBody messageBody = extractMessageBody(messageContent);
        DoStoreGroupMessageDto doStoreGroupMessageDto = new DoStoreGroupMessageDto();
        doStoreGroupMessageDto.setMessageBody(messageBody);
        doStoreGroupMessageDto.setGroupChatMessageContent(messageContent);
        rabbitTemplate.convertAndSend(RabbitmqConstants.STORE_GROUP_MESSAGE,
                "",
                JSONObject.toJSONString(doStoreGroupMessageDto));
        messageContent.setMessageKey(messageBody.getMessageKey());
    }

    /**
     * 通过 MessageId 设置消息缓存
     *
     * @param appId
     * @param messageId
     * @param messageContent
     */
    @Override
    public void setMessageCacheByMessageId(Integer appId, String messageId, Object messageContent) {
        String key = appId + RedisConstants.CACHE_MESSAGE + messageId;
        // 过期时间设置成 5 分钟
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(messageContent), 300, TimeUnit.SECONDS);
    }

    /**
     * 通过 MessageId 获取消息缓存
     *
     * @param appId
     * @param messageId
     * @return
     */
    @Override
    public String getMessageCacheByMessageId(Integer appId, String messageId) {
        String key = appId + RedisConstants.CACHE_MESSAGE + messageId;
        // 先判断是否有这个键值，由于 redis 两种删除策略：惰性删除、定期删除，惰性删除，键值过期依然会有 key，当有线程获取 value 才会删除 key
        // 两种情况：redis 获取不到 value
        // 1. 首次进入，没有设置缓存，not set， getMessageCacheByMessageId == null
        // 2. 重复进入，但是缓存过期，value = null
        Boolean hasKey = stringRedisTemplate.hasKey(key);
        if (hasKey == null || !hasKey) {
            // 没有 key，说明根本没有缓存，或者是定期删除恰好删除了，直接返回 null
            return null;
        }
        Long expireTime = stringRedisTemplate.getExpire(key);
        // 键值已过期
        if (expireTime != null && expireTime <= 0) {
            stringRedisTemplate.delete(key);
            return MessageErrorCode.MESSAGE_CACHE_EXPIRE.getError();
        }
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void storeOfflineMessage(OfflineMessageContent offlineMessage) {
        // 获取 fromId 离线消息队列
        getOfflineMsgQueue(offlineMessage, offlineMessage.getFromId(), offlineMessage.getToId(), ConversationTypeEnum.P2P);
        // 获取 toId 离线消息队列
        getOfflineMsgQueue(offlineMessage, offlineMessage.getToId(), offlineMessage.getFromId(), ConversationTypeEnum.P2P);
    }

    @Override
    public void storeGroupOfflineMessage(OfflineMessageContent offlineMessage, List<String> memberIds) {
        // 对群成员执行 getOfflineMsgQueue 逻辑
        memberIds.forEach(memberId -> getOfflineMsgQueue(
                offlineMessage, memberId,
                offlineMessage.getToId(),
                ConversationTypeEnum.GROUP
        ));
    }

    /**
     * 获取 fromId 的离线消息队列
     *
     * @param offlineMessage
     * @param fromId
     * @param toId
     * @param conversationType
     */
    private void getOfflineMsgQueue(OfflineMessageContent offlineMessage, String fromId, String toId, ConversationTypeEnum conversationType) {
        // 获取用户离线消息队列
        String userKey = offlineMessage.getAppId() + RedisConstants.OFFLINE_MESSAGE + fromId;

        ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
        if (operations.zCard(userKey) > appConfig.getOfflineMessageCount()) {
            // 如果队列数据超过阈值，删除最前面的数据
            operations.removeRange(userKey, 0, 0);
        }

        offlineMessage.setConversationType(conversationType.getCode());
        offlineMessage.setConversationId(conversationServiceImpl.convertConversationId(
                conversationType.getCode(), fromId, toId
        ));
        // 插入数据，messageKey 作为分值
        operations.add(userKey, JSONObject.toJSONString(offlineMessage), offlineMessage.getMessageKey());
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

    /**
     * messageContent 转换成 MessageBody
     *
     * @param messageContent
     * @return
     */
    public MessageBody extractMessageBody(MessageContent messageContent) {
        MessageBody messageBody = new MessageBody();
        messageBody.setAppId(messageContent.getAppId());
        // TODO 消息唯一 ID 通过雪花算法生成
        messageBody.setMessageKey(SnowflakeIdWorker.nextId());
        messageBody.setCreateTime(System.currentTimeMillis());
        // TODO 设置消息加密密钥
        messageBody.setSecurityKey("");
        messageBody.setExtra(messageContent.getExtra());
        messageBody.setDelFlag(DelFlagEnum.NORMAL.getCode());
        messageBody.setMessageTime(messageContent.getMessageTime());
        messageBody.setMessageBody(messageContent.getMessageBody());
        return messageBody;
    }

    private ImMessageBodyEntity getMsgBody(MessageBody messageBody) {
        ImMessageBodyEntity imMessageBodyEntity = new ImMessageBodyEntity();
        imMessageBodyEntity.setAppId(messageBody.getAppId());
        imMessageBodyEntity.setMessageKey(messageBody.getMessageKey());
        imMessageBodyEntity.setMessageBody(messageBody.getMessageBody());
        imMessageBodyEntity.setSecurityKey(messageBody.getSecurityKey());
        imMessageBodyEntity.setMessageTime(messageBody.getMessageTime());
        imMessageBodyEntity.setCreateTime(messageBody.getCreateTime());
        imMessageBodyEntity.setExtra(messageBody.getExtra());
        imMessageBodyEntity.setDelFlag(messageBody.getDelFlag());
        return imMessageBodyEntity;
    }
}

