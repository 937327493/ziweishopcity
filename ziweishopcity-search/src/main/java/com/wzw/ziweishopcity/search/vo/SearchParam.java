package com.wzw.ziweishopcity.search.vo;

import lombok.Data;

import java.util.List;

/**
 *用来接受页面查询条件的模型
 */
@Data
public class SearchParam {
    private String keyword;//页面搜索框传过来的文本 a
    private Long catalog3Id;//页面传过来的三级分类id a
    private String sort;//排序的规则 a
    private Integer hasStock;//是否有库存
    private String skuPrice;//sku的价格 a
    private List<Long> brandId;//品牌id，可以多选 a
    private List<String> attrs;//基本属性，可以多选 a
    private Integer pageNum;//页码 a
}
