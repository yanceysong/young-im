package com.yanceysong.im.common.enums.command;

import com.yanceysong.im.common.constant.CodeAdapter;

/**
 * @ClassName ImSystemCommand
 * @Description
 * @date 2023/4/24 16:38
 * @Author yanceysong
 * @Version 1.0
 */
public enum ImSystemCommand implements CodeAdapter {
    /**
     * 登录 9000 --> 0x2328
     */
    COMMAND_LOGIN(0x2328);

    private final Integer command;

    ImSystemCommand(Integer command) {
        this.command = command;
    }

    @Override
    public Integer getCode() {
        return command;
    }
}
