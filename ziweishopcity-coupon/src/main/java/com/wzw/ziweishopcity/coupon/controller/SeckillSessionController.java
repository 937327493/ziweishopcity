package com.wzw.ziweishopcity.coupon.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.wzw.ziweishopcity.coupon.entity.SeckillSkuRelationEntity;
import com.wzw.ziweishopcity.coupon.entity.SkuFullReductionEntity;
import com.wzw.ziweishopcity.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wzw.ziweishopcity.coupon.entity.SeckillSessionEntity;
import com.wzw.ziweishopcity.coupon.service.SeckillSessionService;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;


/**
 * 秒杀活动场次
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 22:09:51
 */
@RestController
@RequestMapping("coupon/seckillsession")
public class SeckillSessionController {
    @Autowired
    private SeckillSessionService seckillSessionService;

    @GetMapping("/getPromotion3day")
    public R getPromotion3day() {
        List<SeckillSessionEntity> sessions =
                seckillSessionService.getPromotion3day();
        List<SeckillSessionEntity> collectSeckillSession = sessions.stream().map(e -> {
            List<SeckillSkuRelationEntity> seckillSkuRelationEntities = seckillSessionService.getSeckillRelation(e.getId());
            e.setSeckillSkuRelationEntity(seckillSkuRelationEntities);
            return e;
        }).collect(Collectors.toList());
        if (collectSeckillSession != null && collectSeckillSession.size() > 0) {
            //将collectSeckillSession转化为JSON字符串再返回seckill服务
            String jsonString = JSON.toJSONString(collectSeckillSession);
            return R.ok().put("session", jsonString);
        } else
            return R.error();
    }

    /**
     * 列表，分页查询，查到秒杀场次关联的sku
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = seckillSessionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        SeckillSessionEntity seckillSession = seckillSessionService.getById(id);

        return R.ok().put("seckillSession", seckillSession);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SeckillSessionEntity seckillSession) {
        seckillSessionService.save(seckillSession);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SeckillSessionEntity seckillSession) {
        seckillSessionService.updateById(seckillSession);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        seckillSessionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
