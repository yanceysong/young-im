package com.yanceysong.im.infrastructure.zookeeper;

import com.yanceysong.im.codec.config.ImBootstrapConfig;
import com.yanceysong.im.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName ZkRegistry
 * @Description
 * @date 2023/4/26 15:14
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public class ZkRegistry extends Constants.ZkConstants implements Runnable {
    private final ZkManager zkManager;

    private final String ip;

    private final ImBootstrapConfig.TcpConfig tcpConfig;

    public ZkRegistry(ZkManager zkManager, String ip, ImBootstrapConfig.TcpConfig tcpConfig) {
        this.zkManager = zkManager;
        this.ip = ip;
        this.tcpConfig = tcpConfig;
    }

    @Override
    public void run() {
        zkManager.createRootNode();
        String tcpPath = IM_CORE_ZK_ROOT + IM_CORE_ZK_ROOT_TCP + "/" + ip + ":" + tcpConfig.getTcpPort();
        zkManager.createNode(tcpPath);
        log.info("注册 Zk tcpPath 成功, 消息=[{}]", tcpPath);

        String websocketPath = IM_CORE_ZK_ROOT + IM_CORE_ZK_ROOT_WEB + "/" + ip + ":" + tcpConfig.getWebSocketPort();
        zkManager.createNode(websocketPath);
        log.info("注册 Zk websocketPath 成功, 消息=[{}]", websocketPath);
    }
}
