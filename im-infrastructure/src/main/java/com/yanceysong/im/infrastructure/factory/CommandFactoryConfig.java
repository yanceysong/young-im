package com.yanceysong.im.infrastructure.factory;

import com.yanceysong.im.common.enums.command.ImSystemCommand;
import com.yanceysong.im.infrastructure.CommandStrategy;
import com.yanceysong.im.infrastructure.impl.LoginCommand;

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
    }
}
