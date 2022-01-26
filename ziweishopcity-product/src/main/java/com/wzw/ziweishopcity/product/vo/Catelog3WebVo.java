package com.wzw.ziweishopcity.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog3WebVo {
    private String catelog2Id;//2级父分类的id
    private String id;
    private String name;
}
