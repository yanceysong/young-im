package com.yanceysong.im.infrastructure.zookeeper;

import com.alibaba.fastjson.JSON;
import com.yanceysong.im.common.constant.ZkConstants;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName ZkManager
 * @Description
 * @date 2023/4/26 15:13
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
@Component
public class ZkManager extends ZkConstants {
    @Resource
    private final ZkClient zkClient;

    public ZkManager(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    /**
     * 创建父类节点，格式：im-coreRoot/tcp/ip:port
     * 如果没有就新建
     */
    public void createRootNode() {
        boolean rootExists = zkClient.exists(IM_CORE_ZK_ROOT);
        if (!rootExists) {
            zkClient.createPersistent(IM_CORE_ZK_ROOT);
        }
        boolean tcpExists = zkClient.exists(IM_CORE_ZK_ROOT + IM_CORE_ZK_ROOT_TCP);
        if (!tcpExists) {
            zkClient.createPersistent(IM_CORE_ZK_ROOT + IM_CORE_ZK_ROOT_TCP);
        }
        boolean webExists = zkClient.exists(IM_CORE_ZK_ROOT + IM_CORE_ZK_ROOT_WEB);
        if (!webExists) {
            zkClient.createPersistent(IM_CORE_ZK_ROOT + IM_CORE_ZK_ROOT_WEB);
        }
    }

    /**
     * 创建节点, 格式：ip + port
     * @param path 路径
     */
    public void createNode(String path) {
        if (zkClient.exists(path)) {
            zkClient.createPersistent(path);
        }
    }

    /**
     * 从 Zk 获取所有 TCP 服务节点地址
     *
     * @return
     */
    public List<String> getAllTcpNode() {
        List<String> children = zkClient.getChildren(ZkConstants.IM_CORE_ZK_ROOT + ZkConstants.IM_CORE_ZK_ROOT_TCP);
        log.info("Query all [TCP] node =[{}] success.", JSON.toJSONString(children));
        return children;
    }

    /**
     * 从 Zk 获取所有 WEB 服务节点地址
     *
     * @return
     */
    public List<String> getAllWebNode() {
        List<String> children = zkClient.getChildren(ZkConstants.IM_CORE_ZK_ROOT + ZkConstants.IM_CORE_ZK_ROOT_WEB);
        log.info("Query all [WEB] node =[{}] success.", JSON.toJSONString(children));
        return children;
    }
}
