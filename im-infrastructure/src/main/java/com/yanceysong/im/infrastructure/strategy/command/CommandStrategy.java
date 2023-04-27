package com.yanceysong.im.infrastructure.strategy.command;

import com.yanceysong.im.codec.proto.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName CommandStrategy
 * @Description
 * @date 2023/4/25 10:24
 * @Author yanceysong
 * @Version 1.0
 */
public interface CommandStrategy {
    //进行策略实现
    void doStrategy(ChannelHandlerContext ctx, Message msg, Integer brokeId);
}
