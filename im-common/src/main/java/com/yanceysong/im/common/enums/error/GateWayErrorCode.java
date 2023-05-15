package com.yanceysong.im.common.enums.error;

import com.yanceysong.im.common.exception.YoungImExceptionEnum;

/**
 * @ClassName GateWayErrorCode
 * @Description
 * @date 2023/5/15 11:15
 * @Author yanceysong
 * @Version 1.0
 */
public enum GateWayErrorCode implements YoungImExceptionEnum {
    USER_SIGN_NOT_EXIST(60000,"用户签名不存在"),

    APPID_NOT_EXIST(60001,"appId不存在"),

    OPERATOR_NOT_EXIST(60002,"操作人不存在"),

    USER_SIGN_IS_ERROR(60003,"用户签名不正确"),

    USER_SIGN_OPERATE_NOT_MATE(60005,"用户签名与操作人不匹配"),

    USER_SIGN_IS_EXPIRED(60004,"用户签名已过期"),

            ;

    private int code;
    private String error;

    GateWayErrorCode(int code, String error){
        this.code = code;
        this.error = error;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getError() {
        return this.error;
    }
}
