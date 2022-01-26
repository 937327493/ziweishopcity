package com.wzw.ziweishopcity.ziweishopcitythirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableDiscoveryClient
@EnableSwagger2
@EnableFeignClients
@SpringBootApplication
public class ZiweishopcityThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZiweishopcityThirdPartyApplication.class, args);
    }

}
