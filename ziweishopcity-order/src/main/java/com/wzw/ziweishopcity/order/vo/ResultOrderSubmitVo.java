package com.wzw.ziweishopcity.order.vo;

import com.wzw.ziweishopcity.order.entity.OrderEntity;
import lombok.Data;

@Data
public class ResultOrderSubmitVo {
    private OrderEntity orderEntity;
    private Integer resultCode;
}
