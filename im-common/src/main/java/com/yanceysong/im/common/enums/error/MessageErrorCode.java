package com.yanceysong.im.common.enums.error;

import com.yanceysong.im.common.exception.YoungImExceptionEnum;

/**
 * @ClassName MessageErrorCode
 * @Description
 * @date 2023/5/16 9:44
 * @Author yanceysong
 * @Version 1.0
 */
public enum MessageErrorCode implements YoungImExceptionEnum {
    FROMER_IS_MUTE(50002, "发送方被禁言"),

    FROMER_IS_FORBIBBEN(50003, "发送方被禁用"),

    MESSAGE_BODY_IS_NOT_EXIST(50003, "消息体不存在"),

    MESSAGE_RECALL_TIME_OUT(50004, "消息已超过可撤回时间"),

    MESSAGE_IS_RECALLED(50005, "消息已被撤回"),

    MESSAGE_BODY_PERSISTENCE_ERROR(51001, "消息体持久化失败"),

    MESSAGE_HISTORY_PERSISTENCE_ERROR(51002, "历史消息持久化失败"),
    MESSAGE_CACHE_EXPIRE(52001, "消息缓存已过期"),

    ;

    private final int code;
    private final String error;

    MessageErrorCode(int code, String error) {
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
