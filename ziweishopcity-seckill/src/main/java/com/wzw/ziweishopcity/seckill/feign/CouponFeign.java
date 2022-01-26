package com.wzw.ziweishopcity.seckill.feign;

import com.wzw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("ziweishopcity-coupon")
public interface CouponFeign {
    @GetMapping("/coupon/seckillsession/getPromotion3day")
    public R getPromotion3day();
}
