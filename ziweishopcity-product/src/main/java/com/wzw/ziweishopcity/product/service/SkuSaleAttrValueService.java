package com.wzw.ziweishopcity.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.product.entity.SkuSaleAttrValueEntity;
import com.wzw.ziweishopcity.product.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-12-19 15:19:59
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrVo> getSkuItemSaleAttrVos(Long spuId);

    List<String> listSaleAttr(Long skuId);
}

