package com.yanceysong.im.infrastructure.strategy.command.factory;

import com.yanceysong.im.common.enums.command.SystemCommand;
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
        commandStrategyMap.put(SystemCommand.COMMAND_LOGIN.getCommand(), new LoginCommand());
        commandStrategyMap.put(SystemCommand.COMMAND_LOGOUT.getCommand(), new LogoutCommand());
        commandStrategyMap.put(SystemCommand.COMMAND_PING.getCommand(), new PingCommand());
    }
}
