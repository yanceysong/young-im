package com.yanceysong.im.infrastructure.strategy.command.impl;

import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.common.constant.Constants;
import com.yanceysong.im.common.model.UserClientDto;
import com.yanceysong.im.infrastructure.strategy.command.BaseCommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.redis.RedisManager;
import com.yanceysong.im.infrastructure.strategy.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
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
        SessionSocketHolder.removeUserSession((NioSocketChannel) ctx.channel());
    }

}
