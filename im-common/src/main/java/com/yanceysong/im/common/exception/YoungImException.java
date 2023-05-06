package com.yanceysong.im.common.exception;

/**
 * @ClassName ApplicationException
 * @Description
 * @date 2023/4/28 10:55
 * @Author yanceysong
 * @Version 1.0
 */
public class YoungImException extends RuntimeException {
    private final int code;

    private final String error;


    public YoungImException(int code, String message) {
        super(message);
        this.code = code;
        this.error = message;
    }

    public YoungImException(String message) {
        super(message);
        this.code=-1;
        this.error = message;
    }

    public YoungImException(YoungImExceptionEnum exceptionEnum) {
        super(exceptionEnum.getError());
        this.code = exceptionEnum.getCode();
        this.error = exceptionEnum.getError();
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }


    /**
     * avoid the expensive and useless stack trace for api exceptions
     *
     * @see Throwable#fillInStackTrace()
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
