package com.wzw.ziweishopcity.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.wzw.common.es.SkuEs;
import com.wzw.ziweishopcity.search.constant.EsConstant;
import com.wzw.ziweishopcity.search.service.ElasticSearchService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service("elasticSearchService")
public class ElasticSearchServiceImpl implements ElasticSearchService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Override
    public boolean up(List<SkuEs> skuEs) throws IOException {
        //1、用kibana已经保存了映射,创建好了索引
        //2、批量操作存储数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEs skuE : skuEs) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuE.getSkuId().toString());
            String s = JSON.toJSONString(skuE);
            indexRequest.source(s,XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        boolean b = bulk.hasFailures();
        return !b;
    }
}
