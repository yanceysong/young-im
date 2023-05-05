package com.yanceysong.im.common;

import com.yanceysong.im.common.exception.YoungImExceptionEnum;

/**
 * @ClassName BaseErrorCode
 * @Description
 * @date 2023/4/28 11:01
 * @Author yanceysong
 * @Version 1.0
 */
public enum BaseErrorCode implements YoungImExceptionEnum {
    SUCCESS(200, "success"),
    SYSTEM_ERROR(90000, "服务器内部错误,请联系管理员"),
    PARAMETER_ERROR(90001, "参数校验错误"),


    ;

    private final int code;
    private final String error;

    BaseErrorCode(int code, String error) {
        this.code = code;
        this.error = error;
    }

    public int getCode() {
        return this.code;
    }

    public String getError() {
        return this.error;
    }
}
