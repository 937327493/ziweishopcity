package com.wzw.ziweishopcity.search.vo;

import com.wzw.common.es.SkuEs;
import lombok.Data;

import java.util.List;
@Data
public class AttrVo {
    private Long attrId;
    private String attrName;
    private List<String> attrValues;
}
