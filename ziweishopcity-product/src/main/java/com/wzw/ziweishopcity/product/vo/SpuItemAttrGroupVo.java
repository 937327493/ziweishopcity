package com.wzw.ziweishopcity.product.vo;

import lombok.Data;

import java.util.List;
@Data
public class SpuItemAttrGroupVo {
    private String groupName;
    List<SpuBaseAttrVo> attrs;
}
