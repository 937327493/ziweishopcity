package com.wzw.ziweishopcity.search.service;

import com.wzw.common.es.SkuEs;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


public interface ElasticSearchService {
    boolean up(List<SkuEs> skuEs) throws IOException;
}
