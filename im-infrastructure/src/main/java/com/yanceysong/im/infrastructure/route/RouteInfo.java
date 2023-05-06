package com.yanceysong.im.infrastructure.route;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName RouteInfo
 * @Description
 * @date 2023/5/6 10:57
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@AllArgsConstructor
public class RouteInfo {

    private String ip;
    private Integer port;

}