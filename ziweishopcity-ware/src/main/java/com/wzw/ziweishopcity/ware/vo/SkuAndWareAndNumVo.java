package com.wzw.ziweishopcity.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuAndWareAndNumVo {
    private Long skuId;
    private List<Long> wareId;
    private Integer num;
}
