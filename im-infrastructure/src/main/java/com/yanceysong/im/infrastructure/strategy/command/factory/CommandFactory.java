package com.yanceysong.im.infrastructure.strategy.command.factory;

import com.yanceysong.im.infrastructure.strategy.command.CommandStrategy;

/**
 * @ClassName CommandFacotry
 * @Description 指令工厂，策略模式
 * @date 2023/4/25 10:23
 * @Author yanceysong
 * @Version 1.0
 */
public class CommandFactory extends CommandFactoryConfig {

    public CommandStrategy getCommandStrategy(Integer command) {
        return commandStrategyMap.get(command);
    }

}

