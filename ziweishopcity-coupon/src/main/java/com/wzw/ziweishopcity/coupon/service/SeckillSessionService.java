package com.wzw.ziweishopcity.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.coupon.entity.SeckillSessionEntity;
import com.wzw.ziweishopcity.coupon.entity.SeckillSkuRelationEntity;
import com.wzw.ziweishopcity.coupon.entity.SkuFullReductionEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 22:09:51
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {
    PageUtils queryPage(Map<String, Object> params);

    List<SeckillSessionEntity> getPromotion3day();

    List<SeckillSkuRelationEntity> getSeckillRelation(Long id);
}

