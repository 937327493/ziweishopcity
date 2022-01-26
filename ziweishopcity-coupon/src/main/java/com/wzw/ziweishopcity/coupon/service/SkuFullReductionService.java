package com.wzw.ziweishopcity.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.to.SkuReductionTo;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.coupon.entity.SkuFullReductionEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品满减信息
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-12-19 15:29:28
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    void savaSkuReduction(SkuReductionTo skuReductionTo);

    PageUtils queryPage(Map<String, Object> params);

}

