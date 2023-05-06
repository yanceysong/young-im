package com.yanceysong.im.domain;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication(scanBasePackages = {"com.yanceysong.im.infrastructure", "com.yanceysong.im.domain"})
@MapperScan("com.yanceysong.im.domain.*.dao.mapper")
@EnableOpenApi
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
