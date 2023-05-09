package com.yanceysong.im.infrastructure.route.algorithm.random;

import com.yanceysong.im.common.enums.user.UserErrorCode;
import com.yanceysong.im.common.exception.YoungImException;
import com.yanceysong.im.infrastructure.route.RouteHandler;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName RandomHandler
 * @Description 随机
 * @date 2023/5/6 10:59
 * @Author yanceysong
 * @Version 1.0
 */
public class RandomHandler implements RouteHandler {
    @Override
    public String routeServer(List<String> values, String key) {
        int size = values.size();
        if (size == 0) {
            throw new YoungImException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        //随机获取一个
        return values.get(ThreadLocalRandom.current().nextInt(size));
    }
}