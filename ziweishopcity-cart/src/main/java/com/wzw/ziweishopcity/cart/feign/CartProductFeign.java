package com.wzw.ziweishopcity.cart.feign;

import com.wzw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("ziweishopcity-product")
public interface CartProductFeign {
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

    @RequestMapping("/product/skusaleattrvalue/listSaleAttr")
    public R listSaleAttr(@RequestParam("skuId") Long skuId);
}
