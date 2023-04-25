package com.yanceysong.im.codec.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ImBootstrapConfig
 * @Description
 * @date 2023/4/24 16:28
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class ImBootstrapConfig {
    private TcpConfig im;

    /**
     * im:
     * tcpPort: 9001
     * webSocketPort: 19001
     * bossThreadSize: 1
     * workThreadSize: 8
     * <p>
     * redis:
     */
    @Data
    public static class TcpConfig {
        /**
         * tcp 绑定的端口号
         */
        private Integer tcpPort;
        /**
         * websocket 绑定的端口号
         */
        private Integer webSocketPort;
        /**
         * boss 线程数 默认为 1
         */
        private Integer bossThreadSize;
        /**
         * work 线程数
         */
        private Integer workThreadSize;
        /**
         * 心跳超时时间
         */
        private Long heartBeatTime;
        /**
         * redis配置
         */
        private RedisConfig redis;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisConfig {
        /**
         * 单机模式：single 哨兵模式：sentinel 集群模式：cluster
         */
        private String mode;
        /**
         * 数据库
         */
        private Integer database;
        /**
         * 密码
         */
        private String password;
        /**
         * 超时时间
         */
        private Integer timeout;
        /**
         * 最小空闲数
         */
        private Integer poolMinIdle;
        /**
         * 连接超时时间(毫秒)
         */
        private Integer poolConnTimeout;
        /**
         * 连接池大小
         */
        private Integer poolSize;

        /**
         * redis单机配置
         */
        private RedisSingle single;
    }

    /**
     * redis单机配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisSingle {
        /**
         * 地址
         */
        private String address;
    }
}
