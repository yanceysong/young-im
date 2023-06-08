package com.yanceysong.im.infrastructure.strategy.command.system.command.factory;

import com.yanceysong.im.infrastructure.strategy.command.system.SystemCommandStrategy;

/**
 * @ClassName CommandFacotry
 * @Description 指令工厂，策略模式
 * 使用单例模式防止每次读取 channel 都需要初始化 CommandFactory, 所导致的 CPU 飙升
 * @date 2023/4/25 10:23
 * @Author yanceysong
 * @Version 1.0
 */
public class CommandFactory extends CommandFactoryConfig {
    private CommandFactory() {
    }

    private static class SingletonHolder {
        private static final CommandFactory INSTANCE = new CommandFactory();
    }

    public static CommandFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }
    public SystemCommandStrategy getCommandStrategy(Integer command) {
        return commandStrategyMap.get(command);
    }
}

