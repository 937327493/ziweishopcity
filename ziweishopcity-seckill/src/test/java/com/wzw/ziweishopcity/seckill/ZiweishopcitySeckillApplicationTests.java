package com.wzw.ziweishopcity.seckill;

import com.wzw.common.utils.R;
import com.wzw.ziweishopcity.seckill.feign.ProductFeign;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignClient;

@SpringBootTest
class ZiweishopcitySeckillApplicationTests {
    @Autowired
    ProductFeign productFeign;
    @Test
    void contextLoads() {
        R skuInfo = productFeign.getSkuInfo(13L);
        String skuInfo1 = (String) skuInfo.get("skuInfo");
        System.out.println(skuInfo1);
    }

}
