package com.yanceysong.im.infrastructure.zookeeper;

import com.alibaba.fastjson.JSON;
import com.yanceysong.im.common.constant.Constants;
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
public class ZkManager extends Constants.ZkConstants{
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
        boolean rootExists = zkClient.exists(ImCoreZkRoot);
        if (!rootExists) {
            zkClient.createPersistent(ImCoreZkRoot);
        }
        boolean tcpExists = zkClient.exists(ImCoreZkRoot + ImCoreZkRootTcp);
        if (!tcpExists) {
            zkClient.createPersistent(ImCoreZkRoot + ImCoreZkRootTcp);
        }
        boolean webExists = zkClient.exists(ImCoreZkRoot + ImCoreZkRootWeb);
        if (!webExists) {
            zkClient.createPersistent(ImCoreZkRoot + ImCoreZkRootWeb);
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
        List<String> children = zkClient.getChildren(Constants.ZkConstants.ImCoreZkRoot + Constants.ZkConstants.ImCoreZkRootTcp);
        log.info("Query all [TCP] node =[{}] success.", JSON.toJSONString(children));
        return children;
    }

    /**
     * 从 Zk 获取所有 WEB 服务节点地址
     *
     * @return
     */
    public List<String> getAllWebNode() {
        List<String> children = zkClient.getChildren(Constants.ZkConstants.ImCoreZkRoot + Constants.ZkConstants.ImCoreZkRootWeb);
        log.info("Query all [WEB] node =[{}] success.", JSON.toJSONString(children));
        return children;
    }
}
