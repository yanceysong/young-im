package com.yanceysong.im.infrastructure.strategy.command.factory;

import com.yanceysong.im.common.enums.command.GroupEventCommand;
import com.yanceysong.im.common.enums.command.MessageCommand;
import com.yanceysong.im.common.enums.command.SystemCommand;
import com.yanceysong.im.infrastructure.strategy.command.CommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.message.GroupMsgCommand;
import com.yanceysong.im.infrastructure.strategy.command.message.P2PMsgCommand;
import com.yanceysong.im.infrastructure.strategy.command.system.LoginCommand;
import com.yanceysong.im.infrastructure.strategy.command.system.LogoutCommand;
import com.yanceysong.im.infrastructure.strategy.command.system.PingCommand;

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
        // 消息命令策略
        commandStrategyMap.put(MessageCommand.MSG_P2P.getCommand(), new P2PMsgCommand());
        commandStrategyMap.put(GroupEventCommand.MSG_GROUP.getCommand(), new GroupMsgCommand());
    }
}
