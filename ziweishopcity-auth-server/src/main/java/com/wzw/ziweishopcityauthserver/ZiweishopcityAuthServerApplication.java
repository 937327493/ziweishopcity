package com.wzw.ziweishopcityauthserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableRedisHttpSession
public class ZiweishopcityAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZiweishopcityAuthServerApplication.class, args);
    }

}
