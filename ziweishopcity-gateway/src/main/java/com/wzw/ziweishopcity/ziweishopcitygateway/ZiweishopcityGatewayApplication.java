package com.wzw.ziweishopcity.ziweishopcitygateway;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
@EnableDiscoveryClient
public class ZiweishopcityGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZiweishopcityGatewayApplication.class, args);
    }

}
