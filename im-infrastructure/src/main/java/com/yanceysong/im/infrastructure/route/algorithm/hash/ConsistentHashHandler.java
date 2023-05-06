package com.yanceysong.im.infrastructure.route.algorithm.hash;

import com.yanceysong.im.infrastructure.route.RouteHandler;

import java.util.List;

/**
 * @ClassName ConsistentHashHandler
 * @Description 一致性hash算法统一入口
 * @date 2023/5/6 10:59
 * @Author yanceysong
 * @Version 1.0
 */
public class ConsistentHashHandler implements RouteHandler {
    /**
     * 具体的一致性hash算法底层的算法实现，如果要有不同的实现方法。
     * 继承该类即可
     */
    private AbstractConsistentHash hash;

    public void setHash(AbstractConsistentHash hash) {
        this.hash = hash;
    }

    @Override
    public String routeServer(List<String> values, String key) {
        return hash.process(values, key);
    }

}
