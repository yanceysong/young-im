package com.yanceysong.im.common.enums.error;

import com.yanceysong.im.common.exception.YoungImExceptionEnum;

public enum ConversationErrorCode implements YoungImExceptionEnum {

    CONVERSATION_UPDATE_PARAM_ERROR(50000,"会话参数修改错误"),

    ;

    private int code;
    private String error;

    ConversationErrorCode(int code, String error){
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
