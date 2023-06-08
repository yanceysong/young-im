package com.yanceysong.im.infrastructure.strategy.command.system;

import com.yanceysong.im.infrastructure.strategy.command.system.model.CommandExecution;

/**
 * @ClassName CommandStrategy
 * @Description
 * @date 2023/4/25 10:24
 * @Author yanceysong
 * @Version 1.0
 */
public interface SystemCommandStrategy {

    /**
     * 系统命令执行策略接口
     *
     * @param commandExecution 执行指令需要的内容
     */
    void systemStrategy(CommandExecution commandExecution);


}
