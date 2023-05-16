package com.yanceysong.im.infrastructure.strategy.command;

import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.model.ClientInfo;
import com.yanceysong.im.infrastructure.strategy.command.model.CommandExecutionRequest;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @ClassName CommandStrategy
 * @Description
 * @date 2023/4/25 10:24
 * @Author yanceysong
 * @Version 1.0
 */
public interface CommandStrategy {

    /**
     * 系统命令执行策略接口
     * @param commandExecutionRequest
     */
    void systemStrategy(CommandExecutionRequest commandExecutionRequest);


}
