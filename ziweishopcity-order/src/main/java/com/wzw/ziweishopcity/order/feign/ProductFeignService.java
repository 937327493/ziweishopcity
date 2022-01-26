package com.wzw.ziweishopcity.order.feign;

import com.wzw.ziweishopcity.order.vo.SpuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("ziweishopcity-product")
public interface ProductFeignService {
    @RequestMapping("/product/skuinfo/spuBySku")
    public SpuInfoVo spuBySku(@RequestParam("skuId") Long skuId);
}
