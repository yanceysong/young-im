package com.yanceysong.im.infrastructure;

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
    void doStrategy(ChannelHandlerContext ctx, Message msg);
}
