package com.wzw.ziweishopcity.product.dao;

import com.wzw.ziweishopcity.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzw.ziweishopcity.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-12-19 15:19:59
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getSkuItemSaleAttrVos(@Param("spuId") Long spuId);

    List<String> listSaleAttr(@Param("skuId") Long skuId);
}
