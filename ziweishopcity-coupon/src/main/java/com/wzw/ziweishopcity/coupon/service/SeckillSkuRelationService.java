package com.wzw.ziweishopcity.coupon.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.coupon.entity.SeckillSkuRelationEntity;
import com.wzw.ziweishopcity.coupon.entity.SkuFullReductionEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-12-19 15:29:28
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SeckillSkuRelationEntity> getSeckillSkuRelationService(Long id);
}

