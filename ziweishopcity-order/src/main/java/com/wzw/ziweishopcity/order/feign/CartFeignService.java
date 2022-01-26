package com.wzw.ziweishopcity.order.feign;

import com.wzw.ziweishopcity.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("ziweishopcity-cart")
public interface CartFeignService {
    @GetMapping("/item")
    public List<OrderItemVo> item();
}
