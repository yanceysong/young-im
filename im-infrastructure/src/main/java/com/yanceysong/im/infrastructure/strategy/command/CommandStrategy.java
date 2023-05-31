package com.yanceysong.im.infrastructure.strategy.command;

import com.yanceysong.im.infrastructure.strategy.command.model.CommandExecution;

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
     * @param commandExecution
     */
    void systemStrategy(CommandExecution commandExecution);


}
