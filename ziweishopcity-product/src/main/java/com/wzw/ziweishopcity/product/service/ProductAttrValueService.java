package com.wzw.ziweishopcity.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.product.entity.AttrEntity;
import com.wzw.ziweishopcity.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {
    void updateAllSpu(Long spuId, List<ProductAttrValueEntity> attrEntities);
    List<ProductAttrValueEntity> getBaseAttrValueList(Long spuId);
    PageUtils queryPage(Map<String, Object> params);

    void saveAttrProducts(List<ProductAttrValueEntity> collect);

}

