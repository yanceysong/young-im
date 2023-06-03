package com.yanceysong.im.domain.Interceptor;

/**
 * @ClassName RequestHolder
 * @Description
 * @date 2023/5/16 10:31
 * @Author yanceysong
 * @Version 1.0
 */
public class RequestHolder {

    private final static ThreadLocal<Boolean> requestHolder = new ThreadLocal<>();

    public static void set(Boolean isAdmin) {
        requestHolder.set(isAdmin);
    }

    public static Boolean get() {
        return requestHolder.get();
    }

    public static void remove() {
        requestHolder.remove();
    }
}
