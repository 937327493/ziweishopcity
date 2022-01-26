package com.wzw.ziweishopcity.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ZiweishopcityCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZiweishopcityCartApplication.class, args);
    }

}
