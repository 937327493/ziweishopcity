package com.wzw.ziweishopcity.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class BrandGroupAttrRespVo {
    private Long attrGroupId;
    private String attrGroupName;
    private Long sort;
    private String descript;
    private String icon;
    private Long catelogId;

    private List<BrandGroupAttrInnerRespVo> attrs;
}
