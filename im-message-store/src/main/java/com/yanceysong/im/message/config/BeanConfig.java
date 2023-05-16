package com.yanceysong.im.message.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
/**
 * @ClassName BeanConfig
 * @Description
 * @date 2023/5/16 11:01
 * @Author yanceysong
 * @Version 1.0
 */
@Configuration
public class BeanConfig {

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean
    public EasySqlInjector easySqlInjector () {
        return new EasySqlInjector();
    }

}