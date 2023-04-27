package com.yanceysong.im.tcp;

import com.yanceysong.im.codec.config.ImBootstrapConfig;
import com.yanceysong.im.infrastructure.strategy.command.factory.CommandFactoryConfig;
import com.yanceysong.im.infrastructure.redis.RedisManager;
import com.yanceysong.im.infrastructure.rabbitmq.listener.MqMessageListener;
import com.yanceysong.im.infrastructure.utils.MqFactory;
import com.yanceysong.im.infrastructure.zookeeper.ZkManager;
import com.yanceysong.im.infrastructure.zookeeper.ZkRegistry;
import com.yanceysong.im.tcp.server.ImServer;
import com.yanceysong.im.tcp.server.ImWebSocketServer;
import org.I0Itec.zkclient.ZkClient;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ClassName Start
 * @Description
 * @date 2023/4/25 10:02
 * @Author yanceysong
 * @Version 1.0
 */
public class Starter {
    public static void main(String[] args) {
        if (args.length > 0) {
            start(args[0]);
        }
    }

    private static void start(String path) {
        try {
            Yaml yaml = new Yaml();
            FileInputStream is = new FileInputStream(path);
            ImBootstrapConfig config = yaml.loadAs(is, ImBootstrapConfig.class);

            new ImServer(config.getIm()).start();
            new ImWebSocketServer(config.getIm()).start();

            // redisson 在系统启动之初就初始化
            RedisManager.init(config);
            // 策略工厂初始化
            CommandFactoryConfig.init();
            // MQ 工厂初始化
            MqFactory.init(config.getIm().getRabbitmq());
            // MQ 监听器初始化
            MqMessageListener.init();
            // 每个服务器都注册 Zk
            registerZk(config);
        } catch (FileNotFoundException | UnknownHostException e) {
            e.printStackTrace();
            // 程序退出
            System.exit(500);
        }
    }

    /**
     * 对于每一个 IP 地址，都开启一个线程去启动 Zk
     *
     * @param config 配置文件
     * @throws UnknownHostException 异常
     */
    public static void registerZk(ImBootstrapConfig config) throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        ZkClient zkClient = new ZkClient(config.getIm().getZkConfig().getZkAddr()
                , config.getIm().getZkConfig().getZkConnectTimeOut());
        ZkManager zkManager = new ZkManager(zkClient);
        ZkRegistry zkRegistry = new ZkRegistry(zkManager, hostAddress, config.getIm());
        Thread thread = new Thread(zkRegistry);
        thread.start();
    }
}
