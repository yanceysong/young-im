package com.yanceysong.im.infrastructure.strategy.command.factory;

import com.yanceysong.im.common.enums.command.ImSystemCommand;
import com.yanceysong.im.infrastructure.strategy.command.CommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.impl.LoginCommand;
import com.yanceysong.im.infrastructure.strategy.command.impl.LogoutCommand;
import com.yanceysong.im.infrastructure.strategy.command.impl.PingCommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName CommandFactoryConfig
 * @Description
 * @date 2023/4/25 10:24
 * @Author yanceysong
 * @Version 1.0
 */
public class CommandFactoryConfig {
    /**
     * 命令维护策略组
     */
    protected static Map<Integer, CommandStrategy> commandStrategyMap = new ConcurrentHashMap<>();

    public static void init() {
        commandStrategyMap.put(ImSystemCommand.COMMAND_LOGIN.getCode(), new LoginCommand());
        commandStrategyMap.put(ImSystemCommand.COMMAND_LOGOUT.getCode(), new LogoutCommand());
        commandStrategyMap.put(ImSystemCommand.COMMAND_PING.getCode(), new PingCommand());
    }
}
