package com.yanceysong.im.infrastructure.strategy.utils;

import com.yanceysong.im.common.model.UserClientDto;
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
    /**
     * 存储用户与channel的映射关系
     */
    private static final Map<UserClientDto, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();
    private final UserClientDto userClientDto = new UserClientDto();

    public static void put(UserClientDto userClientDto, NioSocketChannel channel) {
        CHANNELS.put(userClientDto, channel);
    }

    public static NioSocketChannel get(UserClientDto userClientDto) {
        return CHANNELS.get(userClientDto);
    }

    /**
     * 根据一个用户删除一个映射关系
     *
     * @param userClientDto 要删除的用户
     */
    public static void remove(UserClientDto userClientDto) {
        CHANNELS.remove(userClientDto);
    }

    /**
     * 根据一个channel删除一个映射关系
     *
     * @param channel 要删除的channel
     */
    public static void remove(NioSocketChannel channel) {
        CHANNELS.entrySet()
                .stream()
                .filter(entity -> entity.getValue() == channel)
                .forEach(entry -> CHANNELS.remove(entry.getKey()));
    }

}
