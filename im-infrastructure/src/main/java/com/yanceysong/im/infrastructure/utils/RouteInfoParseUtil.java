package com.yanceysong.im.infrastructure.utils;

import com.yanceysong.im.common.BaseErrorCode;
import com.yanceysong.im.common.exception.YoungImException;
import com.yanceysong.im.infrastructure.route.RouteInfo;

/**
 * @ClassName RouteInfoParseUtil
 * @Description
 * @date 2023/5/6 11:01
 * @Author yanceysong
 * @Version 1.0
 */
public class RouteInfoParseUtil {
    /**
     * 把ip:port格式的地址解析成为ip和端口号
     *
     * @param info 原始地址
     * @return 解析结果
     */

    public static RouteInfo parse(String info) {
        try {
            String[] serverInfo = info.split(":");
            return new RouteInfo(serverInfo[0], Integer.parseInt(serverInfo[1]));
        } catch (Exception e) {
            throw new YoungImException(BaseErrorCode.PARAMETER_ERROR);
        }
    }

}