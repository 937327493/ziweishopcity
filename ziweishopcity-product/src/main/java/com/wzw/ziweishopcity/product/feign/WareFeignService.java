package com.wzw.ziweishopcity.product.feign;

import com.wzw.common.to.StockNum;
import com.wzw.common.utils.R;
import com.wzw.ziweishopcity.product.entity.SkuInfoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("ziweishopcity-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hastock")
    public List<StockNum> hasTock(@RequestBody List<Long> skuId);
}
