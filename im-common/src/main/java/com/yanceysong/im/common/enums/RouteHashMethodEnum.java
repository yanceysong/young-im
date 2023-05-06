package com.yanceysong.im.common.enums;

import com.yanceysong.im.common.exception.YoungImErrorMsg;
import com.yanceysong.im.common.exception.YoungImException;

/**
 * @ClassName RouteHashMethodEnum
 * @Description
 * @date 2023/5/6 11:19
 * @Author yanceysong
 * @Version 1.0
 */
public enum RouteHashMethodEnum {

    /**
     * TreeMap
     */
    TREE(1, "com.yanceysong.im.service.route.algroithm.hash.TreeMapConsistentHash"),

    /**
     * 自定义map
     */
    CUSTOMER(2, "com.yanceysong.im.service.route.algroithm.hash.xxxx"),

    ;


    private final int code;
    private final String clazz;

    RouteHashMethodEnum(int code, String clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     *
     * @param ordinal 路由的模式
     * @return 路由的枚举类
     */
    public static RouteHashMethodEnum getHandler(int ordinal) {
        for (int i = 0; i < RouteHashMethodEnum.values().length; i++) {
            if (RouteHashMethodEnum.values()[i].getCode() == ordinal) {
                return RouteHashMethodEnum.values()[i];
            }
        }
        throw new YoungImException(YoungImErrorMsg.UNKNOWN_ROUTE_MODEL);
    }

    public String getClazz() {
        return clazz;
    }

    public int getCode() {
        return code;
    }
}

