package com.yanceysong.im.tcp;

import com.yanceysong.im.codec.config.ImBootstrapConfig;
import com.yanceysong.im.infrastructure.factory.CommandFactoryConfig;
import com.yanceysong.im.infrastructure.redis.RedisManager;
import com.yanceysong.im.tcp.server.ImServer;
import com.yanceysong.im.tcp.server.ImWebSocketServer;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @ClassName Start
 * @Description
 * @date 2023/4/25 10:02
 * @Author yanceysong
 * @Version 1.0
 */
public class Starter {
    public static void main(String [] args) {
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
            CommandFactoryConfig.init();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // 程序退出
            System.exit(500);
        }
    }
}
