package com.wzw.ziweishopcity.product;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"com.wzw.common.config","com.wzw.ziweishopcity.product"})
@MapperScan("com.wzw.ziweishopcity.product.dao")
@EnableSwagger2
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableRedisHttpSession
public class ZiweishopcityProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZiweishopcityProductApplication.class, args);
    }
}
