package com.wzw.ziweishopcity.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class DoneVo {
    private Long id;
    private List<DoneItemVo> items;
}
