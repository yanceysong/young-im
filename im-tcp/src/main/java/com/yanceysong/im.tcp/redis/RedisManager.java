package com.yanceysong.im.tcp.redis;

import com.yanceysong.im.codec.config.ImBootstrapConfig;
import org.redisson.api.RedissonClient;

/**
 * @ClassName RedisManager
 * @Description
 * @date 2023/4/25 9:57
 * @Author yanceysong
 * @Version 1.0
 */
public class RedisManager {

    private static RedissonClient redissonClient;

    public static void init(ImBootstrapConfig config) {
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redissonClient = singleClientStrategy.getRedissonClient(config.getIm().getRedis());
    }

    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }

}
