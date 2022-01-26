package com.wzw.ziweishopcity.seckill.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 秒杀活动商品关联
 * 
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-12-19 15:29:28
 */
@Data
public class SeckillSkuRelationEntityVo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
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

	private Integer seckillSort;
}
