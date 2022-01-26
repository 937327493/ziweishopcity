package com.wzw.ziweishopcity.order.feign;

import com.wzw.common.to.StockNum;
import com.wzw.ziweishopcity.order.vo.HasStockVo;
import com.wzw.ziweishopcity.order.vo.SkuAndNumVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("ziweishopcity-ware")
public interface WmsFeignServcie {
    @PostMapping("/ware/waresku/hastock")
    public List<HasStockVo> hasTock(@RequestBody List<Long> skuId);

    @PostMapping("/ware/waresku/lock/order")//order服务创建订单需要锁定库存
    public Boolean lockOrder(@RequestBody List<SkuAndNumVo> skuAndNumVos, @RequestParam("orderSn") String orderSn);
}
