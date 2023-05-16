package com.yanceysong.im.infrastructure.config;

import com.yanceysong.im.common.enums.route.RouteHashMethodEnum;
import com.yanceysong.im.common.enums.route.UrlRouteModelEnum;
import com.yanceysong.im.infrastructure.route.RouteHandler;
import com.yanceysong.im.infrastructure.route.algorithm.hash.AbstractConsistentHash;
import com.yanceysong.im.infrastructure.supports.ids.SnowflakeIdWorker;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @ClassName BeanConfig
 * @Description
 * @date 2023/5/5 10:34
 * @Author yanceysong
 * @Version 1.0
 */
@Configuration
public class BeanConfig {
    @Resource
    private AppConfig appConfig;

    @Bean
    public ZkClient buildZkClient() {
        return new ZkClient(appConfig.getZkAddr(), appConfig.getZkConnectTimeOut());
    }

    @Bean
    public RouteHandler routeHandler() throws Exception {
        //获取路由的模式
        Integer imRouteModel = appConfig.getImRouteModel();
        // 配置文件指定使用哪种路由策略
        UrlRouteModelEnum handler = UrlRouteModelEnum.getHandler(imRouteModel);
        // 获得对应模式的类路径
        String routeModelClassPath = handler.getClazz();
        // 反射机制调用具体的类对象执行对应方法
        RouteHandler routeHandler = (RouteHandler) Class.forName(routeModelClassPath).getDeclaredConstructor().newInstance();
        // 特判，一致性哈希可以指定底层数据结构
        if (UrlRouteModelEnum.HASH.equals(handler)) {
            //找到对应setHash这个方法
            Method setHash = Class.forName(routeModelClassPath).getMethod("setHash", AbstractConsistentHash.class);
            Integer consistentHashModel = appConfig.getConsistentHashModel();
            //拿到一致性hash底层的数据结构
            RouteHashMethodEnum hashHandler = RouteHashMethodEnum.getHandler(consistentHashModel);
            //拿到对应数据结构实现类的全路径
            String hashModelClassPath = hashHandler.getClazz();
            AbstractConsistentHash consistentHash = (AbstractConsistentHash) Class.forName(hashModelClassPath).getDeclaredConstructor().newInstance();
            //将具体的一致性hash算法的实现类添加进去
            setHash.invoke(routeHandler, consistentHash);
        }
        return routeHandler;
    }
    @Bean
    public SnowflakeIdWorker buildSnowflakeSeq() {
        return new SnowflakeIdWorker(0);
    }

}
