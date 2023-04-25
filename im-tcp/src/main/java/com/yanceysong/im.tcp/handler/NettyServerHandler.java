package com.yanceysong.im.tcp.handler;

import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.infrastructure.strategy.command.CommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.factory.CommandFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName NettyServerHandler
 * @Description
 * @date 2023/4/25 9:54
 * @Author yanceysong
 * @Version 1.0
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    private final static Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        //解析获得指令
        Integer command = parseCommand(msg);
        CommandFactory commandFactory = new CommandFactory();
        //根据指令选择对应的策略模式
        CommandStrategy commandStrategy = commandFactory.getCommandStrategy(command);
        commandStrategy.doStrategy(ctx, msg);
    }

    protected Integer parseCommand(Message msg) {
        return msg.getMessageHeader().getCommand();
    }

}
