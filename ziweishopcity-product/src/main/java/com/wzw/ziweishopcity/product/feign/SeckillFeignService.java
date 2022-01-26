package com.wzw.ziweishopcity.product.feign;

import com.wzw.ziweishopcity.product.vo.SeckillRedisTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("ziweishopcity-seckill")
public interface SeckillFeignService {
    @GetMapping("/productGetSeckillRedisTo/{skuId}")
    SeckillRedisTo productGetSeckillRedisTo(@PathVariable("skuId") String skuId);
}
