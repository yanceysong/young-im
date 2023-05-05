package com.yanceysong.im.common.enums.user;

/**
 * @ClassName UserSilentFlagEnum
 * @Description
 * @date 2023/4/28 10:59
 * @Author yanceysong
 * @Version 1.0
 */
public enum UserSilentFlagEnum {
    /**
     * 0 正常；1 禁言。
     */
    NORMAL(0),

    MUTE(1),
    ;

    private final int code;

    UserSilentFlagEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
