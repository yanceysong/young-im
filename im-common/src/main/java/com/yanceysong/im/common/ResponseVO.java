package com.yanceysong.im.common;

import com.yanceysong.im.common.exception.YoungImExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ResponseVO
 * @Description
 * @date 2023/4/28 11:02
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO {
    public static final int SUCCESS = 200;
    public static final int FAIL = 500;
    private static final String SUCCESS_MSG = "success";
    private static final String FAIL_MSG = "fail";
    private int code;

    private String msg;

    private Object data;

    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
//		this.data = null;
    }

    public static ResponseVO successResponse(String msg, Object data) {
        return new ResponseVO(SUCCESS, msg, data);
    }

    public static ResponseVO successResponse() {
        return new ResponseVO(SUCCESS, SUCCESS_MSG);
    }

    public static ResponseVO successResponse(String msg) {
        return new ResponseVO(SUCCESS, msg);
    }

    public static ResponseVO successResponse(Object data) {
        return new ResponseVO(SUCCESS, SUCCESS_MSG, data);
    }

    public static ResponseVO errorResponse() {
        return new ResponseVO(FAIL, FAIL_MSG);
    }

    public static ResponseVO errorResponse(int code, String msg) {
        return new ResponseVO(code, msg);
    }
    public static ResponseVO errorResponse( String msg) {
        return new ResponseVO(FAIL, msg);
    }
    public static ResponseVO errorResponse(YoungImExceptionEnum enums) {
        return new ResponseVO(enums.getCode(), enums.getError());
    }

    public boolean isOk() {
        return this.code == SUCCESS;
    }

    public ResponseVO success() {
        this.code = SUCCESS;
        this.msg = SUCCESS_MSG;
        return this;
    }

    public ResponseVO success(Object data) {
        this.code = SUCCESS;
        this.msg = SUCCESS_MSG;
        this.data = data;
        return this;
    }
}
