package com.yanceysong.im.infrastructure.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yanceysong.im.codec.config.ImBootstrapConfig;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName MqFactory
 * @Description
 * @date 2023/4/26 14:02
 * @Author yanceysong
 * @Version 1.0
 */
public class MqFactory {
    private static final Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    private static ConnectionFactory factory = null;

    private static Connection getConnection() throws IOException, TimeoutException {
        return factory.newConnection();
    }

    /**
     * 通过channel名字获取一个channel
     *
     * @param channelName channel名字
     * @return channel
     * @throws IOException      异常
     * @throws TimeoutException 异常
     */
    public static Channel getChannel(String channelName) throws IOException, TimeoutException {
        Channel channel = channelMap.get(channelName);
        if (channel == null) {
            channel = getConnection().createChannel();
            channelMap.put(channelName, channel);
        }
        return channel;
    }

    /**
     * 初始化直接从配置文件读取信息进行初始化
     *
     * @param rabbitmq mq配置文件
     */
    public static void init(ImBootstrapConfig.Rabbitmq rabbitmq) {
        if (factory == null) {
            factory = new ConnectionFactory();
            factory.setHost(rabbitmq.getHost());
            factory.setPort(rabbitmq.getPort());
            factory.setUsername(rabbitmq.getUserName());
            factory.setPassword(rabbitmq.getPassword());
            factory.setVirtualHost(rabbitmq.getVirtualHost());
        }
    }
}
