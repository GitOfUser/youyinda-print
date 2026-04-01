package com.youyinda;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.youyinda.mapper")
@EnableFeignClients(basePackages = "com.youyinda.feign")
@EnableScheduling
public class YouyindaApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouyindaApplication.class, args);
    }
}
