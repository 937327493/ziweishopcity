package com.wzw.ziweishopcity.member;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"com.wzw.common.config","com.wzw.ziweishopcity.member"})
@EnableFeignClients(basePackages = {"com.wzw.ziweishopcity.member.feign"})
@MapperScan("com.wzw.ziweishopcity.member.dao")
@EnableSwagger2
@EnableDiscoveryClient
public class ZiweishopcityMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZiweishopcityMemberApplication.class, args);
    }

}
