package com.yanceysong.im.infrastructure.strategy.command.model;

import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.infrastructure.feign.FeignMessageService;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * @ClassName CommandExecutionRequest
 * @Description
 * @date 2023/5/16 11:26
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class CommandExecutionRequest {

    private ChannelHandlerContext ctx;

    private Message msg;

    private Integer brokeId;

    private FeignMessageService feignMessageService;

}
