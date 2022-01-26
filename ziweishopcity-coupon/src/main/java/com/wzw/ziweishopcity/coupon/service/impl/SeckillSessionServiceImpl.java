package com.wzw.ziweishopcity.coupon.service.impl;

import com.wzw.ziweishopcity.coupon.entity.SeckillSkuRelationEntity;
import com.wzw.ziweishopcity.coupon.entity.SkuFullReductionEntity;
import com.wzw.ziweishopcity.coupon.service.SeckillSkuRelationService;
import com.wzw.ziweishopcity.coupon.service.SkuFullReductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.coupon.dao.SeckillSessionDao;
import com.wzw.ziweishopcity.coupon.entity.SeckillSessionEntity;
import com.wzw.ziweishopcity.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {
    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 得到三天内所有秒杀场次的信息
     *
     * @return
     */
    @Override
    public List<SeckillSessionEntity> getPromotion3day() {
        String startDatatime = getStartDatatime();
        String endDatatime = getEndDatatime();
        List<SeckillSessionEntity> list = this
                .list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startDatatime, endDatatime));
        return list;
    }

    /**
     * 根据商品场次id获取该商品场次中的秒杀商品信息
     *
     * @param id
     * @return
     */
    @Override
    public List<SeckillSkuRelationEntity> getSeckillRelation(Long id) {
        List<SeckillSkuRelationEntity> seckillSkuRelationEntities = seckillSkuRelationService.getSeckillSkuRelationService(id);
        return seckillSkuRelationEntities;
    }

    /**
     * 得到秒杀的起始时间
     *
     * @return
     */
    private String getStartDatatime() {
        LocalTime min = LocalTime.MIN;
        LocalDate now = LocalDate.now();
        LocalDateTime of = LocalDateTime.of(now, min);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String format = dateTimeFormatter.format(of);
        return format;
    }

    /**
     * 得到秒杀的终止时间
     *
     * @return
     */
    private String getEndDatatime() {
        LocalTime max = LocalTime.MAX;
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(2);
        LocalDateTime of = LocalDateTime.of(localDate, max);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String format = dateTimeFormatter.format(of);
        return format;
    }
}