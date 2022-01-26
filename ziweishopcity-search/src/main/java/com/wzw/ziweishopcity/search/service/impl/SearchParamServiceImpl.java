package com.wzw.ziweishopcity.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.wzw.common.es.SkuEs;
import com.wzw.ziweishopcity.search.constant.EsConstant;
import com.wzw.ziweishopcity.search.service.SearchParamService;
import com.wzw.ziweishopcity.search.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.util.StringHelper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchParamServiceImpl implements SearchParamService {
    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResult search(SearchParam searchParam) {
        SearchResult searchResult = null;
        //1、根据前端的查询请求构建dsl查询语句
        SearchRequest searchQuery = getSearchQuery(searchParam);
        System.out.println(searchQuery.source());
        //2、通过dsl查询语句查询es
        try {
            SearchResponse search = client.search(searchQuery, RequestOptions.DEFAULT);
            //3、构建返回客户端前端的模型数据
            searchResult = getSearchResult(search, searchParam);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //4、返回前端
        return searchResult;
    }

    private SearchResult getSearchResult(SearchResponse searchResponse, SearchParam searchParam) {
        SearchResult searchResult = new SearchResult();
        //先拿到查询结果
        Integer pageNum = searchParam.getPageNum();//前端请求的当前页
        if (pageNum != null)
            searchResult.setPageNum(pageNum);
        long value = searchResponse.getHits().getTotalHits().value;//总记录数
        searchResult.setTotal(value);
        int i = value % 2 == 0 ? ((int) value % 2) : ((int) (value % 2 + 1));//总页数——计算得到
        searchResult.setTotalPages(i);
        //再拿到聚合信息
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<SkuEs> skuEsList = new ArrayList<>();
        if (hits != null && hits.length > 0) {
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                SkuEs skuEs = JSON.parseObject(sourceAsString, SkuEs.class);
                if (StringUtils.isNotEmpty(searchParam.getKeyword()))//如果前端搜索条件包括了keyword则必须高亮
                {
                    HighlightField title = hit.getHighlightFields().get("title");
                    String setsku = title.getFragments()[0].string();
                    skuEs.setTitle(setsku);
                }
                skuEsList.add(skuEs);
            }
            searchResult.setProducts(skuEsList);
        }
        Aggregations aggregations = searchResponse.getAggregations();
        //商品分类聚合信息
        ParsedLongTerms catalog_agg = aggregations.get("catalog_agg");
        ArrayList<CatalogVo> catalogVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            CatalogVo catalogVo = new CatalogVo();
            catalogVo.setCatalogId(Long.valueOf(bucket.getKeyAsString()));
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name");
            Terms.Bucket bucket1 = catalog_name_agg.getBuckets().get(0);
            catalogVo.setCatalogName(bucket1.getKeyAsString());
            catalogVos.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVos);
        //品牌聚合信息
        ParsedLongTerms brand_agg = aggregations.get("brand_agg");
        ArrayList<BrandVo> brandVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets1 = brand_agg.getBuckets();
        for (Terms.Bucket bucket : buckets1) {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(bucket.getKeyAsNumber().longValue());
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name");
            brandVo.setBrandName(brand_name_agg.getBuckets().get(0).getKeyAsString());
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img");
            brandVo.setBrandImg(brand_img_agg.getBuckets().get(0).getKeyAsString());
            brandVos.add(brandVo);
        }
        searchResult.setBrands(brandVos);
        //属性聚合信息
        ParsedNested attr_agg = aggregations.get("attr_agg");
        ArrayList<AttrVo> attrVos = new ArrayList<>();
        ParsedLongTerms parsedLongTerms = attr_agg.getAggregations().get("attr_id_agg");
        List<? extends Terms.Bucket> buckets2 = parsedLongTerms.getBuckets();
        for (Terms.Bucket bucket : buckets2) {
            AttrVo attrVo = new AttrVo();
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            ParsedStringTerms parsedStringTerms = bucket.getAggregations().get("attr_name_agg");
            attrVo.setAttrName(parsedStringTerms.getBuckets().get(0).getKeyAsString());
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            List<? extends Terms.Bucket> buckets3 = attr_value_agg.getBuckets();
            ArrayList<String> strings = null;
            for (Terms.Bucket bucket1 : buckets3) {
                strings = new ArrayList<>();
                strings.add(bucket1.getKeyAsString());
            }
            attrVo.setAttrValues(strings);
            attrVos.add(attrVo);
        }
        searchResult.setAttrs(attrVos);
        ArrayList<Integer> integers = new ArrayList<>();
        for (Integer integer = 1; integer <= searchResult.getTotalPages(); integer++) {
            integers.add(integer);
        }
        searchResult.setPageNavs(integers);
        return searchResult;
    }

    /**
     * 将前端发来的查询请求解析成SearchRequest
     *
     * @param searchParam
     */
    private SearchRequest getSearchQuery(SearchParam searchParam) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();//构建查询
        //1、构建bool - query,支持多条件的查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //2、创建must匹配
        if (StringUtils.isNotEmpty(searchParam.getKeyword())) {
            boolQueryBuilder
                    .must(QueryBuilders.matchQuery("title", searchParam.getKeyword()));
        }
        //3、创建filter匹配
        //3.1根据类型id
        if (searchParam.getCatalog3Id() != null) {
            boolQueryBuilder
                    .filter(QueryBuilders.termQuery("catelogId", searchParam.getCatalog3Id()));
        }
        //3.2根据品牌id
        if (searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0) {
            boolQueryBuilder
                    .filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }
        //3.3根据是否有库存,前端传来1为有库存，0为无库存
        if (searchParam.getHasStock() != null) {
            boolQueryBuilder
                    .filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));
        }
        //3.4根据价格区间
        if (StringUtils.isNotEmpty(searchParam.getSkuPrice())) {
            RangeQueryBuilder skuPrice = QueryBuilders.rangeQuery("skuPrice");
            String[] s = searchParam.getSkuPrice().split("_");
            if (s.length == 2) {
                skuPrice.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (searchParam.getSkuPrice().startsWith("_")) {
                    skuPrice.lte(s[0]);
                } else if (searchParam.getSkuPrice().endsWith("_")) {
                    skuPrice.gte(s[0]);
                }
            }
            boolQueryBuilder.filter(skuPrice);
        }
        //3.4根据属性  nested设置为不参与评分ScoreMode.None
        if (searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0) {
            if (searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0) {
                List<String> attrs1 = searchParam.getAttrs();
                for (String attrStr : attrs1) {
                    BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
                    String[] s = attrStr.split("_");
                    String attrId = s[0];//这里是属性的id
                    String[] attrValues = s[1].split(":");//这里是属性的值
                    boolQueryBuilder1.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                    boolQueryBuilder1.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                    //每个属性都必须生产一个nestedquery
                    NestedQueryBuilder attrs = QueryBuilders
                            .nestedQuery("attrs", boolQueryBuilder1, ScoreMode.None);
                    boolQueryBuilder.filter(attrs);
                }
            }
        }
        searchSourceBuilder.query(boolQueryBuilder);//把查询归于根
        //4、排序匹配
        if (searchParam.getSort() != null) {
            String[] s = searchParam.getSort().split("_");
            String s1 = s[0];//排序的skuId
            SortOrder sortOrders = s[1]
                    .equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s[0], sortOrders);
        }

        //5、根据分页匹配
        if (searchParam.getPageNum() != null) {
            searchSourceBuilder.from((searchParam.getPageNum() - 1) * 2);
            searchSourceBuilder.size(2);
        }
        //6、高亮，如果有模糊匹配keyword才使用高亮
        if (StringUtils.isNotEmpty(searchParam.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title");
            highlightBuilder.preTags("<b style='color:red;'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        //7、聚合分析、根据聚合匹配
        //7.1、品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId");
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brand_agg);
        //7.2、分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catelogId");
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name").field("catelogName").size(1));
        searchSourceBuilder.aggregation(catalog_agg);
        //7.3、属性聚合
        NestedAggregationBuilder nestedAggregationBuilder =
                new NestedAggregationBuilder("attr_agg", "attrs");
        TermsAggregationBuilder attridagg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attridagg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attridagg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        nestedAggregationBuilder.subAggregation(attridagg);
        searchSourceBuilder.aggregation(nestedAggregationBuilder);
        //结束  参数是索引名 SearchSourceBuilder
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
    }
}
