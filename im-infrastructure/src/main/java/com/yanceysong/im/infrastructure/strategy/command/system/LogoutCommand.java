package com.yanceysong.im.infrastructure.strategy.command.system;

import com.yanceysong.im.infrastructure.strategy.command.BaseCommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.model.CommandExecution;
import com.yanceysong.im.infrastructure.utils.UserChannelRepository;
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
    public void systemStrategy(CommandExecution commandExecution) {
        ChannelHandlerContext ctx = commandExecution.getCtx();
        UserChannelRepository.remove(ctx.channel());
    }

}
