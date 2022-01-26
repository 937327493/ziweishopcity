package com.wzw.ziweishopcity.search.controller;

import com.wzw.common.es.SkuEs;
import com.wzw.common.utils.R;
import com.wzw.ziweishopcity.search.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/search")
public class ElasticSearchController {
    @Autowired
    ElasticSearchService elasticSearchService;
    @PostMapping("/search/listSave")
    public R up(@RequestBody List<SkuEs> skuEs) throws IOException {
        boolean up = elasticSearchService.up(skuEs);
        if (up == true)
            return R.ok();
        else
            return R.error();
    }
}
