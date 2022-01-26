package com.wzw.ziweishopcity.search.vo;

import com.wzw.common.es.SkuEs;
import lombok.Data;

import java.util.List;
@Data
public class SearchResult {
    private List<SkuEs> products;//商品的sku信息
    private Long total;//检索到的商品数量
    private Integer pageNum = 1;//当前页码
    private Integer totalPages;//所有页码
    private List<BrandVo> brands;//所有品牌信息
    private List<AttrVo> attrs;//所有基本属性信息
    private List<CatalogVo> catalogs;//所有三级分类信息
    private List<Integer> pageNavs;//所有存在的页码
}
