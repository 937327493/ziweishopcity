package com.wzw.ziweishopcity.seckill.feign;

import com.wzw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("ziweishopcity-product")
public interface ProductFeign {
    @RequestMapping("/product/skuinfo/secSkuid")
    public R getSkuInfo(@RequestParam("skuId") Long skuId);
}
