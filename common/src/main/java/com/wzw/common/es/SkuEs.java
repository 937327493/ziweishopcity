package com.wzw.common.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuEs {
    private Long skuId;
    private Long spuId;
    private String title;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private Long catelogId;
    private String brandName;
    private String brandImg;
    private String catelogName;
    private List<Attrs> attrs;//基本属性信息
}
