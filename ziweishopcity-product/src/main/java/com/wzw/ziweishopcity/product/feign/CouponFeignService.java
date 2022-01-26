package com.wzw.ziweishopcity.product.feign;

import com.wzw.common.to.SkuReductionTo;
import com.wzw.common.to.SpuBoundTo;
import com.wzw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("ziweishopcity-coupon")
public interface CouponFeignService {
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);
}
