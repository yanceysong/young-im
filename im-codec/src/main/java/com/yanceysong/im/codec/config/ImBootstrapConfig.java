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
//    private GlobalHttpClientConfig httpClientConfig;

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
         * Feign RPC 连接 TCP层和业务层内部地址
         */
        private String logicUrl;
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

        /**
         * rabbitmq 配置
         */
        private Rabbitmq rabbitmq;
        /**
         * zk 配置
         */
        private ZkConfig zkConfig;
        /**
         * 分布式 Id 区分服务
         */
        private Integer brokerId;
        /**
         * 端登录策略类型
         */
        private Integer loginModel;
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

    @Data
    public static class Rabbitmq {
        /**
         * 主机地址
         */
        private String host;
        /**
         * 端口号
         */
        private Integer port;
        /**
         * 虚拟主机地址
         */
        private String virtualHost;
        /**
         * 用户名
         */
        private String userName;
        /**
         * 密码
         */
        private String password;
        /**
         * zk 配置
         */
        private ZkConfig zkConfig;
    }

    @Data
    public static class ZkConfig {
        /**
         * zk 连接地址
         */
        private String zkAddr;
        /**
         * zk 连接超时时间
         */
        private Integer zkConnectTimeOut;
    }
}
