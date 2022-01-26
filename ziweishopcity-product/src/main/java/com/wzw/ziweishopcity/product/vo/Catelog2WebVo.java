package com.wzw.ziweishopcity.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2WebVo {
    private String catelog1Id;//1级父分类的id
    private List<Catelog3WebVo> catalog3List;//3级子分类的集合
    private String id;
    private String name;
}
