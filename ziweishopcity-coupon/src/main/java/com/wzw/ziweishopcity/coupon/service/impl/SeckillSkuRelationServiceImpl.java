package com.wzw.ziweishopcity.coupon.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzw.ziweishopcity.coupon.entity.SkuFullReductionEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.coupon.dao.SeckillSkuRelationDao;
import com.wzw.ziweishopcity.coupon.entity.SeckillSkuRelationEntity;
import com.wzw.ziweishopcity.coupon.service.SeckillSkuRelationService;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String promotionSessionId = (String) params.get("promotionSessionId");
        QueryWrapper<SeckillSkuRelationEntity> qw = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (key != null && StringUtils.isNotEmpty(key)) {
            qw.and(wrapper -> wrapper.like("promotion_id", key).or().like("sku_id", key));
        }
        if (promotionSessionId != null && StringUtils.isNotEmpty(promotionSessionId)) {
            qw.eq("promotion_session_id", promotionSessionId);
        }
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                qw
        );
        return new PageUtils(page);
    }

    @Override
    public List<SeckillSkuRelationEntity> getSeckillSkuRelationService(Long id) {
        List<SeckillSkuRelationEntity> promotion_session_id = this
                .list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
        return promotion_session_id;
    }

}