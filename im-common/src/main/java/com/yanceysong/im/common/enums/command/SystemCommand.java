package com.yanceysong.im.common.enums.command;

/**
 * @ClassName ImSystemCommand
 * @Description
 * @date 2023/4/24 16:38
 * @Author yanceysong
 * @Version 1.0
 */
public enum SystemCommand implements Command {
    /**
     * 登录 9000 --> 0x2328
     */
    COMMAND_LOGIN(0x2328),
    /**
     * 登出 9003 --> 0x232b
     */
    COMMAND_LOGOUT(0x232b),
    /**
     * 心跳 9999 --> 0x270f
     */
    COMMAND_PING(0x270f),
    /**
     * 消息发送 9005 --> 0x232d
     */
    SEND_MSG(0x232d),
    /**
     * 下线通知 用于多端互斥 9002 --> 0x232a
     */
    MUTA_LOGIN(0x232a);
    private final Integer command;

    SystemCommand(Integer command) {
        this.command = command;
    }

    @Override
    public Integer getCommand() {
        return command;
    }
}
