package com.yanceysong.im.tcp.handler;

import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.infrastructure.feign.FeignMessageService;
import com.yanceysong.im.infrastructure.rabbitmq.publish.MqMessageProducer;
import com.yanceysong.im.infrastructure.strategy.command.system.SystemCommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.system.command.factory.CommandFactory;
import com.yanceysong.im.infrastructure.strategy.command.system.model.CommandExecution;
import com.yanceysong.im.infrastructure.utils.UserChannelRepository;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @ClassName NettyServerHandler
 * @Description
 * @date 2023/4/25 9:54
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    private final Integer brokerId;

    private final FeignMessageService feignMessageService;
    /**
     * 采用对象池复用对象，防止在启动项目时 CPU 占用率飙升
     */
    private final GenericObjectPool<CommandExecution> commandExecutionRequestPool
            = new GenericObjectPool<>(new CommandExecutionFactory());

    public NettyServerHandler(Integer brokerId, String logicUrl) {
        this.brokerId = brokerId;
        feignMessageService = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                // 设置超时时间
                .options(new Request.Options(1000, 3500))
                .target(FeignMessageService.class, logicUrl);
    }

    /**
     * 有读消息来的时候
     *
     * @param ctx 上下文
     * @param msg 消息
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Integer command = parseCommand(msg);
        CommandFactory commandFactory = CommandFactory.getInstance();
        SystemCommandStrategy systemCommandStrategy = commandFactory.getCommandStrategy(command);
        CommandExecution commandExecution = null;
        try {
            // 从对象池中获取 CommandExecution 对象
            commandExecution = commandExecutionRequestPool.borrowObject();
            commandExecution.setCtx(ctx);
            commandExecution.setBrokeId(brokerId);
            commandExecution.setMsg(msg);
            commandExecution.setFeignMessageService(feignMessageService);
            if (systemCommandStrategy != null) {
                // 执行策略
                systemCommandStrategy.systemStrategy(commandExecution);
            } else {
                //发送消息
                MqMessageProducer.sendMessage(msg, command);
            }
        } finally {
            // 将对象归还给对象池
            if (commandExecution != null) {
                commandExecutionRequestPool.returnObject(commandExecution);
            }
        }
    }

    /**
     * 新上线一个channel
     *
     * @param ctx 上    下文
     * @throws Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UserChannelRepository.add(ctx.channel());
    }

    /**
     * channel不活跃了
     *
     * @param ctx 上下文
     * @throws Exception 异常
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        UserChannelRepository.remove(ctx.channel());
//        logger.info("剩余通道个数：{}", UserChannelRepository.CHANNEL_GROUP.size());
    }

    /**
     * 异常处理
     *
     * @param ctx   上下文
     * @param cause 什么原因
     * @throws Exception 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        UserChannelRepository.remove(ctx.channel());
    }

    /**
     * 根据发过来的消息获取到消息的指令
     *
     * @param msg 消息
     * @return 指令
     */
    protected Integer parseCommand(Message msg) {
        return msg.getMessageHeader().getCommand();
    }

    /**
     * CommandExecution 对象工厂
     */
    private static class CommandExecutionFactory extends BasePooledObjectFactory<CommandExecution> {
        @Override
        public CommandExecution create() throws Exception {
            return new CommandExecution();
        }

        @Override
        public PooledObject<CommandExecution> wrap(CommandExecution obj) {
            return new DefaultPooledObject<>(obj);
        }
    }

}
