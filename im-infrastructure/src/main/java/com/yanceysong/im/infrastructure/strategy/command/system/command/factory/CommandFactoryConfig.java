package com.yanceysong.im.infrastructure.strategy.command.system.command.factory;

import com.yanceysong.im.common.enums.command.GroupEventCommand;
import com.yanceysong.im.common.enums.command.MessageCommand;
import com.yanceysong.im.common.enums.command.SystemCommand;
import com.yanceysong.im.infrastructure.strategy.command.system.SystemCommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.system.command.GroupMsgSystemCommand;
import com.yanceysong.im.infrastructure.strategy.command.system.command.P2PMsgSystemCommand;
import com.yanceysong.im.infrastructure.strategy.command.system.command.LoginSystemCommand;
import com.yanceysong.im.infrastructure.strategy.command.system.command.LogoutSystemCommand;
import com.yanceysong.im.infrastructure.strategy.command.system.command.PingSystemCommand;

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
    protected static Map<Integer, SystemCommandStrategy> commandStrategyMap = new ConcurrentHashMap<>();
    private static final LoginSystemCommand loginCommand = new LoginSystemCommand();
    private static final LogoutSystemCommand logoutCommand = new LogoutSystemCommand();
    private static final PingSystemCommand pingCommand = new PingSystemCommand();
    private static final P2PMsgSystemCommand p2PMsgCommand = new P2PMsgSystemCommand();
    private static final GroupMsgSystemCommand groupMsgCommand = new GroupMsgSystemCommand();

    public static void init() {
        commandStrategyMap.put(SystemCommand.COMMAND_LOGIN.getCommand(), loginCommand);
        commandStrategyMap.put(SystemCommand.COMMAND_LOGOUT.getCommand(), logoutCommand);
        commandStrategyMap.put(SystemCommand.COMMAND_PING.getCommand(), pingCommand);
        // 消息命令策略
        commandStrategyMap.put(MessageCommand.MSG_P2P.getCommand(), p2PMsgCommand);
        commandStrategyMap.put(GroupEventCommand.MSG_GROUP.getCommand(), groupMsgCommand);

    }
}
