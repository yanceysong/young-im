package com.yanceysong.im.common.exception;

/**
 * @ClassName ApplicationExceptionEnum
 * @Description
 * @date 2023/4/28 10:56
 * @Author yanceysong
 * @Version 1.0
 */
public interface YoungImExceptionEnum {
    /**
     * 获取枚举代号
     * @return 枚举代号
     */
    int getCode();

    /**
     * 获取枚举内容
     * @return 枚举内容
     */
    String getError();
}
