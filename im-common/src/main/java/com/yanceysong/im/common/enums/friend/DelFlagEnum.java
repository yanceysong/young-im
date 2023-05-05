package com.yanceysong.im.common.enums.friend;

/**
 * @ClassName DelFlagEnum
 * @Description
 * @date 2023/4/28 10:54
 * @Author yanceysong
 * @Version 1.0
 */
public enum DelFlagEnum {
    /**
     * 0 正常；1 删除。
     */
    NORMAL(0),

    DELETE(1),
    ;

    private final int code;

    DelFlagEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
