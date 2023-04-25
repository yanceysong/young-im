package com.yanceysong.im.tcp.utils;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName SessionSocketHolder
 * @Description
 * @date 2023/4/25 10:01
 * @Author yanceysong
 * @Version 1.0
 */
public class SessionSocketHolder {
    private static final Map<String, NioSocketChannel> CHANNELS = new ConcurrentHashMap<String, NioSocketChannel>();

    public static void put(String userId, NioSocketChannel channel) {
        CHANNELS.put(userId, channel);
    }

    public static NioSocketChannel get(String userId) {
        return CHANNELS.get(userId);
    }
}
