package com.yanceysong.im.domain;

import com.yanceysong.im.domain.message.strategy.factory.DomainCommandFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

import javax.annotation.Resource;

@SpringBootApplication(scanBasePackages = {"com.yanceysong.im.infrastructure", "com.yanceysong.im.domain"})
@MapperScan("com.yanceysong.im.domain.*.dao.mapper")
@EnableOpenApi
public class Application implements CommandLineRunner {
    @Resource
    private DomainCommandFactory domainCommandFactory;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //在这里初始化策略工厂
        domainCommandFactory.init();
    }
}
