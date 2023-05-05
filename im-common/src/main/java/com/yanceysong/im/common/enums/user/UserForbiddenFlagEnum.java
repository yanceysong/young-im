package com.yanceysong.im.common.enums.user;

/**
 * @ClassName UserForbiddenFlagEnum
 * @Description
 * @date 2023/4/28 10:59
 * @Author yanceysong
 * @Version 1.0
 */
public enum UserForbiddenFlagEnum {
    /**
     * 0 正常；1 禁用。
     */
    NORMAL(0),

    FORBIBBEN(1),
    ;

    private final int code;

    UserForbiddenFlagEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
