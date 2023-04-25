package com.yanceysong.im.infrastructure.strategy.command.impl;

import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.common.constant.Constants;
import com.yanceysong.im.common.model.UserClientDto;
import com.yanceysong.im.infrastructure.strategy.command.BaseCommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.redis.RedisManager;
import com.yanceysong.im.infrastructure.strategy.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

/**
 * @ClassName LogoutCommand
 * @Description
 * @date 2023/4/25 11:20
 * @Author yanceysong
 * @Version 1.0
 */
public class LogoutCommand extends BaseCommandStrategy {
    @Override
    public void doStrategy(ChannelHandlerContext ctx, Message msg) {
        // 删除 Session and redisson 里的 session
        String userId = (String) ctx.channel().attr(AttributeKey.valueOf(Constants.UserClientConstants.UserId)).get();
        Integer appId = (Integer) ctx.channel().attr(AttributeKey.valueOf(Constants.UserClientConstants.AppId)).get();
        Integer clientType = (Integer) ctx.channel().attr(AttributeKey.valueOf(Constants.UserClientConstants.ClientType)).get();
        //获取这个实体
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);
        //删除session
        SessionSocketHolder.remove(userClientDto);
        //删除redisson
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<Object, Object> map = redissonClient.getMap(appId + Constants.RedisConstants.UserSessionConstants + userId);
        // 删除 Hash 里的 key，key 存放用户的 Session
        map.remove(clientType);
        ctx.close();
    }

}
