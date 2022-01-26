package com.wzw.ziweishopcity.order.vo;

import com.wzw.ziweishopcity.order.entity.OrderEntity;
import com.wzw.ziweishopcity.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateVo {
    private OrderEntity orderEntity;
    private List<OrderItemEntity> orderItemEntities;
    private BigDecimal finalPrice;
}
