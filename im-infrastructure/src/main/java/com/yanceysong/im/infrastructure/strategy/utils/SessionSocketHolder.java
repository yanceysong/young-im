package com.yanceysong.im.infrastructure.strategy.utils;

import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.common.constant.Constants;
import com.yanceysong.im.common.enums.connect.ImSystemConnectState;
import com.yanceysong.im.common.model.UserClientDto;
import com.yanceysong.im.common.model.UserSession;
import com.yanceysong.im.infrastructure.strategy.command.redis.RedisManager;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

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
        //找到这个channel然后直接remove
        CHANNELS.entrySet()
                .stream()
                .filter(entity -> entity.getValue() == channel)
                .forEach(entry -> CHANNELS.remove(entry.getKey()));
    }

    /**
     * 用户登出(离线)
     *
     * @param nioSocketChannel channel
     */
    public static void removeUserSession(NioSocketChannel nioSocketChannel) {
        // 删除 Session and redisson 里的 session
        String userId = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ChannelConstants.UserId)).get();
        Integer appId = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ChannelConstants.AppId)).get();
        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ChannelConstants.ClientType)).get();

        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);

        SessionSocketHolder.remove(userClientDto);
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(appId + Constants.RedisConstants.UserSessionConstants + userId);
        // 删除 Hash 里的 key，key 存放用户的 Session
        map.remove(clientType.toString());
        nioSocketChannel.close();
    }

    /**
     * 用户退后台
     *
     * @param nioSocketChannel channel
     */
    public static void offlineUserSession(NioSocketChannel nioSocketChannel) {
        // 删除 Session and redisson 里的 session
        String userId = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ChannelConstants.UserId)).get();
        Integer appId = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ChannelConstants.AppId)).get();
        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ChannelConstants.ClientType)).get();

        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);

        SessionSocketHolder.remove(userClientDto);
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(appId + Constants.RedisConstants.UserSessionConstants + userId);
        String sessionStr = map.get(clientType.toString());
        //设置新的状态
        if (!StringUtils.isBlank(sessionStr)) {
            UserSession userSession = JSONObject.parseObject(sessionStr, UserSession.class);
            userSession.setConnectState(ImSystemConnectState.CONNECT_STATE_OFFLINE.getCode());
            map.put(clientType.toString(), JSONObject.toJSONString(userSession));
        }
        nioSocketChannel.close();
    }

}
