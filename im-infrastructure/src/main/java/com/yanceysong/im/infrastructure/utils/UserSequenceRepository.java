package com.yanceysong.im.infrastructure.utils;

import com.yanceysong.im.common.constant.RedisConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName UserSequenceRepository
 * @Description
 * @date 2023/5/18 16:54
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class UserSequenceRepository {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

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

}

