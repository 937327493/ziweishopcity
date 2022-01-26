package com.wzw.ziweishopcity.order;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"com.wzw.common.config","com.wzw.ziweishopcity.order"})
@EnableDiscoveryClient
@EnableSwagger2
@EnableFeignClients
@MapperScan("com.wzw.ziweishopcity.order.dao")
public class ZiweishopcityOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZiweishopcityOrderApplication.class, args);
    }

}
