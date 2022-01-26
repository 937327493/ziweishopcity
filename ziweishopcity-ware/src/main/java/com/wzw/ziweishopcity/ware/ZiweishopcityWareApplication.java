package com.wzw.ziweishopcity.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"com.wzw.common.config","com.wzw.ziweishopcity.ware"})
@MapperScan("com.wzw.ziweishopcity.ware.dao")
@EnableSwagger2
@EnableFeignClients
@EnableDiscoveryClient
public class ZiweishopcityWareApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZiweishopcityWareApplication.class, args);
    }
}
