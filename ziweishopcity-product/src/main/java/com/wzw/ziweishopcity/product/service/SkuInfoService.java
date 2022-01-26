package com.wzw.ziweishopcity.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.product.entity.SkuInfoEntity;
import com.wzw.ziweishopcity.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    SpuInfoEntity getSpuBySku(Long skuId);

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

}

