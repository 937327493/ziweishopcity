package com.wzw.ziweishopcity.product.feign;

import com.wzw.common.es.SkuEs;
import com.wzw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@FeignClient("ziweishopcity-search")
public interface SearchFeignService {
    @PostMapping("/search/search/listSave")
    public R up(@RequestBody List<SkuEs> skuEs) ;
}
