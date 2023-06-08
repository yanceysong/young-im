package com.yanceysong.im.infrastructure.strategy.command.system.command;

import com.yanceysong.im.common.constant.ChannelConstants;
import com.yanceysong.im.infrastructure.strategy.command.system.model.CommandExecution;
import com.yanceysong.im.infrastructure.strategy.command.system.BaseSystemCommandStrategy;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @ClassName PingCommand
 * @Description
 * @date 2023/4/25 14:46
 * @Author yanceysong
 * @Version 1.0
 */
public class PingSystemCommand extends BaseSystemCommandStrategy {
    @Override
    public void systemStrategy(CommandExecution commandExecution) {
        ChannelHandlerContext ctx = commandExecution.getCtx();
        /*
         *channel 绑定当前时间
         */
        ctx.channel()
                .attr(AttributeKey
                        .valueOf(ChannelConstants.READ_TIME))
                .set(System.currentTimeMillis());
    }
}
