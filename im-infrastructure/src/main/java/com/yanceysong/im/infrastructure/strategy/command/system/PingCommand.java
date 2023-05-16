package com.yanceysong.im.infrastructure.strategy.command.system;

import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.common.constant.Constants;
import com.yanceysong.im.infrastructure.strategy.command.BaseCommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.model.CommandExecutionRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @ClassName PingCommand
 * @Description
 * @date 2023/4/25 14:46
 * @Author yanceysong
 * @Version 1.0
 */
public class PingCommand extends BaseCommandStrategy {
    @Override
    public void systemStrategy(CommandExecutionRequest commandExecutionRequest) {
        ChannelHandlerContext ctx = commandExecutionRequest.getCtx();
        /*
         *channel 绑定当前时间
         */
        ctx.channel()
                .attr(AttributeKey
                        .valueOf(Constants.ChannelConstants.READ_TIME))
                .set(System.currentTimeMillis());
    }
}
