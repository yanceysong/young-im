package com.yanceysong.im.infrastructure.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.common.constant.RedisConstants;
import com.yanceysong.im.common.model.read.ImGroupConversationEntity;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName UserSequenceRepository
 * @Description
 * @date 2023/5/18 16:54
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class UserCacheRepository {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private Redisson redisson;

    /**
     * 记录用户所有模块: 好友、群组、会话的数据偏序
     * Redis Hash 记录
     * uid 做 key, 具体 seq 做 value
     *
     * @param appId
     * @param userId
     * @param type
     * @param seq
     */
    public void writeUserSeq(Integer appId, String userId, String type, Long seq) {
        String key = appId + RedisConstants.SEQ_PRE_FIX + userId;
        redisTemplate.opsForHash().put(key, type, seq);
    }

    /**
     * 更新某个群组
     *
     * @param appId      appid
     * @param readUserId 已读人id
     * @param messageId  消息id
     */
    public void updateGroupMessageReadUserRecord(Integer appId, String readUserId, JSONObject conversationJson, String messageId) {
        //组装key
        String key = appId + RedisConstants.CONVERSATION_MESSAGE_READ_RECORD + conversationJson.get("conversationId");
        //查看会话缓存
        String groupConversationEntityJson = redisTemplate.opsForValue().get(key);
        ImGroupConversationEntity imGroupConversationEntity;
        //没有缓存设置缓存加进去
        if (StringUtils.isBlank(groupConversationEntityJson)) {
            imGroupConversationEntity = new ImGroupConversationEntity();
               HashMap<String, List<String>> map = new HashMap<>();
            map.put(messageId, new ArrayList<>() {{
                add(readUserId);
            }});
            imGroupConversationEntity.setMessageReadUserMap(map);
        } else {
            //如果之前有缓存的消息
            imGroupConversationEntity = JSONObject.parseObject(groupConversationEntityJson, ImGroupConversationEntity.class);
            imGroupConversationEntity.getMessageReadUserMap().get(messageId).add(readUserId);
        }
        //缓存会话消息
        redisTemplate.opsForValue().set(key, JSON.toJSONString(imGroupConversationEntity));
    }

    public ImGroupConversationEntity getImGroupConversationReadList(Integer appId,String conversationId) {
        //组装key
        String key = appId + RedisConstants.CONVERSATION_MESSAGE_READ_RECORD +conversationId;
        //查看会话缓存
        String groupConversationEntityJson = redisTemplate.opsForValue().get(key);
        ImGroupConversationEntity imGroupConversationEntity;
        //没有缓存设置缓存加进去
        if (StringUtils.isBlank(groupConversationEntityJson)) {
            imGroupConversationEntity = new ImGroupConversationEntity();
          } else {
            //如果之前有缓存的消息
            imGroupConversationEntity = JSONObject.parseObject(groupConversationEntityJson, ImGroupConversationEntity.class);
        }
        return imGroupConversationEntity;
    }

}

