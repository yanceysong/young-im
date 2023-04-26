package com.yanceysong.im.infrastructure.strategy.command.impl;

import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.infrastructure.strategy.command.BaseCommandStrategy;
import com.yanceysong.im.infrastructure.strategy.utils.UserChannelRepository;
import io.netty.channel.ChannelHandlerContext;

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
        UserChannelRepository.remove(ctx.channel());
    }

}
