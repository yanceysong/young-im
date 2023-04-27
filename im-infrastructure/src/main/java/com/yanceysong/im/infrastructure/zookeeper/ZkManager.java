package com.yanceysong.im.infrastructure.zookeeper;

import com.yanceysong.im.common.constant.Constants;
import org.I0Itec.zkclient.ZkClient;

/**
 * @ClassName ZkManager
 * @Description
 * @date 2023/4/26 15:13
 * @Author yanceysong
 * @Version 1.0
 */
public class ZkManager extends Constants.ZkConstants{
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
}
