package com.yanceysong.im.common.enums;

import com.yanceysong.im.common.exception.YoungImErrorMsg;
import com.yanceysong.im.common.exception.YoungImException;

/**
 * @ClassName UrlRouteModelEnum
 * @Description
 * @date 2023/5/6 10:49
 * @Author yanceysong
 * @Version 1.0
 */
public enum UrlRouteModelEnum {
    /**
     * 随机
     */
    RANDOM(1, "com.yacneysong.im.service.route.algroithm.random.RandomHandler"),
    /**
     * 轮询
     */
    LOOP(2, "com.yacneysong.im.service.route.algroithm.loop.LoopHandler"),
    /**
     * 一致性 HASH
     */
    HASH(3, "com.yacneysong.im.service.route.algroithm.hash.ConsistentHashHandler"),
    ;
    private final int code;
    private final String clazz;

    UrlRouteModelEnum(int code, String clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     *
     * @param ordinal 路由模式
     * @return 对应的路由枚举类
     */
    public static UrlRouteModelEnum getHandler(int ordinal) {
        for (int i = 0; i < UrlRouteModelEnum.values().length; i++) {
            if (UrlRouteModelEnum.values()[i].getCode() == ordinal) {
                return UrlRouteModelEnum.values()[i];
            }
        }
        throw new YoungImException(YoungImErrorMsg.UNKNOWN_ROUTE_MODEL);
    }

    public int getCode() {
        return code;
    }

    public String getClazz() {
        return clazz;
    }
}
