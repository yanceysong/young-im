package com.yanceysong.im.tcp.handler;

import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.infrastructure.feign.FeignMessageService;
import com.yanceysong.im.infrastructure.strategy.command.CommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.factory.CommandFactory;
import com.yanceysong.im.infrastructure.strategy.command.model.CommandExecutionRequest;
import com.yanceysong.im.infrastructure.utils.UserChannelRepository;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

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
    private String logicUrl;

    private FeignMessageService feignMessageService;
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
        CommandFactory commandFactory = new CommandFactory();
        CommandStrategy commandStrategy = commandFactory.getCommandStrategy(command);
        // 使用 req 包装参数内部传参，避免后期新增参数需要扩展接口字段
        CommandExecutionRequest commandExecutionRequest = new CommandExecutionRequest();
        commandExecutionRequest.setCtx(ctx);
        commandExecutionRequest.setBrokeId(brokerId);
        commandExecutionRequest.setMsg(msg);
        commandExecutionRequest.setFeignMessageService(feignMessageService);

        // 执行策略
        commandStrategy.systemStrategy(commandExecutionRequest);
    }

    /**
     * 新上线一个channel
     *
     * @param ctx 上下文
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

}
