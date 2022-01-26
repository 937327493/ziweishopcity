package com.wzw.ziweishopcity.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillOrderVo {
    /**
     * 订单id
     */
    private String orderSn;
    /**
     * 用户名
     */
    private String username;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private Integer num;
}
