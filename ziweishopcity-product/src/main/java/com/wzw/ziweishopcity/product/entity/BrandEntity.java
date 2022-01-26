package com.wzw.ziweishopcity.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.wzw.common.valid.Create;
import com.wzw.common.valid.ListValue;
import com.wzw.common.valid.Update;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.validation.Validator;

/**
 * 品牌
 * 
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 品牌id
	 */
//	@NotNull(message = "修改品牌信息必须指定品牌id",groups = {Update.class})
//	@Null(message = "新增品牌信息不能指定品牌id",groups = {Create.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
//	@NotBlank(message = "新增和修改品牌信息必须指定品牌id",groups = {Update.class,Create.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
//	@ListValue(vals={0,1})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	private String firstLetter;
	/**
	 * 排序
	 */
	private Integer sort;

}
