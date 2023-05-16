package com.yanceysong.im.common.enums.user;

/**
 * @ClassName UserTypeEnum
 * @Description
 * @date 2023/5/16 9:52
 * @Author yanceysong
 * @Version 1.0
 */
public enum UserTypeEnum {
    IM_USER(1),

    APP_ADMIN(100),
    ;

    private int code;

    UserTypeEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
