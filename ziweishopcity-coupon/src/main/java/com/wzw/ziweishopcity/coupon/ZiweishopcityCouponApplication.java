package com.wzw.ziweishopcity.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@MapperScan("com.wzw.ziweishopcity.coupon.dao")
@SpringBootApplication(scanBasePackages = {"com.wzw.common.config","com.wzw.ziweishopcity.coupon"})
@EnableSwagger2
@EnableDiscoveryClient
@EnableFeignClients
public class ZiweishopcityCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZiweishopcityCouponApplication.class, args);
    }
}