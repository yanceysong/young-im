package com.yanceysong.im.infrastructure.redis;

import com.yanceysong.im.codec.config.ImBootstrapConfig;
import com.yanceysong.im.infrastructure.rabbitmq.listener.UserLoginMessageListener;
import org.redisson.api.RedissonClient;

/**
 * @ClassName RedisManager
 * @Description 客户端管理类
 * @date 2023/4/25 9:57
 * @Author yanceysong
 * @Version 1.0
 */
public class RedisManager {

    private static RedissonClient redissonClient;

    public static void init(ImBootstrapConfig config) {
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        //初始化redisson
        redissonClient = singleClientStrategy.getRedissonClient(config.getIm().getRedis());
        // 初始化监听类
        UserLoginMessageListener userLoginMessageListener = new UserLoginMessageListener(config.getIm().getLoginModel());
        //redisManager启动的时候就会开启监听，每上线一个用户，就会执行该用户的多端登录策略
        userLoginMessageListener.listenerUserLogin();
    }

    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }

}
