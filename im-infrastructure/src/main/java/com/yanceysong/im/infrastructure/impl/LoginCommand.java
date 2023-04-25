package com.yanceysong.im.infrastructure.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yanceysong.im.codec.pack.LoginPack;
import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.common.constant.Constants;
import com.yanceysong.im.common.enums.connect.ImSystemConnectState;
import com.yanceysong.im.common.model.UserSession;
import com.yanceysong.im.infrastructure.BaseCommandStrategy;
import com.yanceysong.im.infrastructure.redis.RedisManager;
import com.yanceysong.im.infrastructure.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

/**
 * @ClassName LoginCommand
 * @Description
 * @date 2023/4/25 10:25
 * @Author yanceysong
 * @Version 1.0
 */
public class LoginCommand extends BaseCommandStrategy {
    @Override
    public void doStrategy(ChannelHandlerContext ctx, Message msg) {
        // 解析 msg
        LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()),
                new TypeReference<LoginPack>() {

                }.getType());
        // channel 设置属性
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(loginPack.getUserId());
        // 将 channel 存起来
        SessionSocketHolder.put(loginPack.getUserId(), (NioSocketChannel) ctx.channel());

        // Redisson 高速存储用户 Session
        UserSession userSession = new UserSession();
        userSession.setUserId(loginPack.getUserId());
        userSession.setAppId(msg.getMessageHeader().getAppId());
        userSession.setClientType(msg.getMessageHeader().getClientType());
        userSession.setConnectState(ImSystemConnectState.CONNECT_STATE_OFFLINE.getCode());
        // 存储到 Redis
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(
                msg.getMessageHeader().getAppId() +
                        Constants.RedisConstants.UserSessionConstants +
                        loginPack.getUserId());
        map.put(msg.getMessageHeader().getClientType() + "",
                JSONObject.toJSONString(userSession));
    }
}
