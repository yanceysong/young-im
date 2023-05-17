package com.yanceysong.im.domain.message.seq;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName RedisSequence
 * @Description 原子自增序列号
 * @date 2023/5/17 11:17
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class RedisSequence {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取序列号
     *
     * @param key 自增序列号的key
     * @return 如果成功获取则返回序列号，失败则返回-1
     */
    public long doGetSeq(String key) {
        Long increment = stringRedisTemplate.opsForValue().increment(key);
        return increment != null ? increment : -1L;
    }

}