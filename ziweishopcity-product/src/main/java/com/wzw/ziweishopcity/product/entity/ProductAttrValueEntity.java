package com.wzw.ziweishopcity.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * spu属性值
 * 
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
@Data
@TableName("pms_product_attr_value")
public class ProductAttrValueEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 商品id
	 */
	private Long spuId;
	/**
	 * 属性id
	 */
	private Long attrId;
	/**
	 * 属性名
	 */
	private String attrName;
	/**
	 * 属性值
	 */
	private String attrValue;
	/**
	 * 顺序
	 */
	private Integer attrSort;
	/**
	 * 快速展示【是否展示在介绍上；0-否 1-是】
	 */
	private Integer quickShow;

}
