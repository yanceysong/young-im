package com.yanceysong.im.infrastructure.route.algroithm.loop;

import com.yanceysong.im.common.BaseErrorCode;
import com.yanceysong.im.common.exception.YoungImException;
import com.yanceysong.im.infrastructure.route.RouteHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName LoopHandler
 * @Description
 * @date 2023/5/6 10:58
 * @Author yanceysong
 * @Version 1.0
 */
public class LoopHandler implements RouteHandler {

    private final AtomicLong index = new AtomicLong();

    @Override
    public String routeServer(List<String> values, String key) {
        int size = values.size();
        if (size == 0) {
            throw new YoungImException(BaseErrorCode.PARAMETER_ERROR);
        }
        long l = index.incrementAndGet() % size;
        if (l < 0) {
            l = 0L;
        }
        return values.get((int) l);
    }
}