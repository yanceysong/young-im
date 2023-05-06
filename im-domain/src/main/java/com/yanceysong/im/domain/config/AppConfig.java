package com.yanceysong.im.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName AppConfig
 * @Description
 * @date 2023/5/6 10:51
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "appconfig")
public class AppConfig {

    /**
     * zk 连接地址
     */
    private String zkAddr;

    /**
     * zk 最大超时时长
     */
    private Integer zkConnectTimeOut;

    /**
     * im 管道路由策略
     */
    private Integer imRouteModel;
    /**
     * 一致性哈希所使用的底层数据结构
     */
    private Integer consistentHashModel;

}
