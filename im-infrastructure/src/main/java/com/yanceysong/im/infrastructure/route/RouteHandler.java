package com.yanceysong.im.infrastructure.route;

import java.util.List;

/**
 * @ClassName RouteHandler
 * @Description
 * @date 2023/5/6 10:57
 * @Author yanceysong
 * @Version 1.0
 */
public interface RouteHandler {
    String routeServer(List<String> values, String key);
}
