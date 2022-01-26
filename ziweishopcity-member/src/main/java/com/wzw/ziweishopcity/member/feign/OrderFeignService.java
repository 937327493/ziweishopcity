package com.wzw.ziweishopcity.member.feign;

import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;
import com.wzw.ziweishopcity.member.vo.OrderItemEntityVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient("ziweishopcity-order")
public interface OrderFeignService {
    @RequestMapping("/order/order/orderPageShow")
    public String orderPageShow(@RequestBody Map<String, Object> params);
}
