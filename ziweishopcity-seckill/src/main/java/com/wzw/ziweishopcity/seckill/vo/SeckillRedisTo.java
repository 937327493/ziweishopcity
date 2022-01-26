package com.wzw.ziweishopcity.seckill.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeckillRedisTo {
    /**
     * 活动id
     */
    private Long promotionId;
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
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    private SkuInfoVo skuInfoVo;

    /**
     * 每日开始时间
     */
    private String startTime;
    /**
     * 每日结束时间
     */
    private String endTime;

    /**
     * 随机码
     */
    private String randomKey;
}
