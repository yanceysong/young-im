package com.yanceysong.im.tcp;

import com.yanceysong.im.codec.config.ImBootstrapConfig;
import com.yanceysong.im.common.exception.YoungImErrorMsg;
import com.yanceysong.im.common.exception.YoungImException;
import com.yanceysong.im.infrastructure.rabbitmq.listener.MqMessageListener;
import com.yanceysong.im.infrastructure.redis.RedisManager;
import com.yanceysong.im.infrastructure.strategy.command.factory.CommandFactoryConfig;
import com.yanceysong.im.infrastructure.rabbitmq.MqFactory;
import com.yanceysong.im.infrastructure.zookeeper.ZkManager;
import com.yanceysong.im.infrastructure.zookeeper.ZkRegistry;
import com.yanceysong.im.tcp.server.ImServer;
import com.yanceysong.im.tcp.server.ImWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.yaml.snakeyaml.Yaml;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ClassName Start
 * @Description 在jdk9以上的版本中，由于引入了模块化设计
 * 导致netty不能够正常启动
 * 需要再启动选项添加jvm参数即可解决
 * --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dio.netty.tryReflectionSetAccessible=true
 * @date 2023/4/25 10:02
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public class Starter {
    private static final String YOUNG_IM_VERSION = "v1.0";

    public static void main(String[] args) {
        // 忽略unsafe类的警告
        disableWarning();
        if (args.length > 0) {
            start(args[0]);
        } else {
            throw new YoungImException(YoungImErrorMsg.CONFIG_NOT_FOUND);
        }
    }

    private static void start(String path) {
        try (FileInputStream is = new FileInputStream(path);) {
            Yaml yaml = new Yaml();
            ImBootstrapConfig config = yaml.loadAs(is, ImBootstrapConfig.class);
            new ImServer(config.getIm()).start();
            new ImWebSocketServer(config.getIm()).start();
            // redisson 在系统启动之初就初始化
            RedisManager.init(config);
            log.info("初始化redisson成功");
            // 策略工厂初始化
            CommandFactoryConfig.init();
            log.info("初始化策略工厂成功");
            // MQ 工厂初始化
            MqFactory.init(config.getIm().getRabbitmq());
            log.info("初始化rabbitmq成功");
            // MQ 监听器初始化
            MqMessageListener.init(String.valueOf(config.getIm().getBrokerId()));
            log.info("初始化mq监听器成功");
            // 每个服务器都注册 Zk
            registerZk(config);
            log.info("初始化zk成功");
            System.out.println(getWelcomePrint());
        } catch (Exception e) {
            e.printStackTrace();
            // 程序退出
            System.exit(500);
        }
    }

    /**
     * 忽略unsafe类警告
     */
    private static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);
            Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
            // ignore
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

    /**
     * 获取logo的本文
     *
     * @return logo的文本
     * @throws IOException io异常
     */
    private static String getLogo() throws IOException {
        StringBuilder builder = new StringBuilder();
        InputStream is = Starter.class.getClassLoader().getResourceAsStream("banner.txt");
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s;
        while ((s = br.readLine()) != null) {
            builder.append(s).append("\n");
        }
        return builder.toString();
    }

    /**
     * 获取欢迎页面
     *
     * @return 包裹
     */
    public static String getWelcomePrint() throws IOException {
        return getLogo()
                + getVersion()
                + getPoem()
                + " Welcome to Young IM Gateway " + YOUNG_IM_VERSION + "!";
    }

    /**
     * 返回打油诗
     *
     * @return 打油诗
     */
    private static String getPoem() {
        return "                  写字楼里写字间,写字间里程序员;\n" +
                "                  程序人员写程序,又拿程序换酒钱.\n" +
                "                  酒醒只在网上坐,酒醉还来网下眠;\n" +
                "                  酒醉酒醒日复日,网上网下年复年.\n" +
                "                  但愿老死电脑间,不愿鞠躬老板前;\n" +
                "                  奔驰宝马贵者趣,公交自行程序员.\n" +
                "                  别人笑我太疯癫,我笑自己命太贱;\n" +
                "                  不见满街漂亮妹,哪个归得程序员?\n\n";
    }

    /**
     * 获得版本信息
     *
     * @return 版本信息
     */
    private static String getVersion() {
        return "  ==========::Young IM::===========                 " + (YOUNG_IM_VERSION.contains("Beta") ? "" : "     ") + "(" + YOUNG_IM_VERSION + ")\n";
    }
}
