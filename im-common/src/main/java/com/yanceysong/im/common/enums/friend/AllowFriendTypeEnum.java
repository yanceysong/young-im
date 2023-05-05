package com.yanceysong.im.common.enums.friend;

/**
 * @ClassName AllowFriendTypeEnum
 * @Description
 * @date 2023/4/28 10:53
 * @Author yanceysong
 * @Version 1.0
 */
public enum AllowFriendTypeEnum {
    /**
     * 验证
     */
    NEED(2),

    /**
     * 不需要验证
     */
    NOT_NEED(1),

    ;


    private final int code;

    AllowFriendTypeEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
