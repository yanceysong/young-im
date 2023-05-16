package com.yanceysong.im.infrastructure.strategy.command.system;

import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.infrastructure.strategy.command.BaseCommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.model.CommandExecutionRequest;
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
    public void systemStrategy(CommandExecutionRequest commandExecutionRequest) {
        ChannelHandlerContext ctx = commandExecutionRequest.getCtx();
        UserChannelRepository.remove(ctx.channel());
    }

}
