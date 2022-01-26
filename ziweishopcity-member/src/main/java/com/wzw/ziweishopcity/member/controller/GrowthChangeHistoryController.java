package com.wzw.ziweishopcity.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.wzw.ziweishopcity.member.feign.CouponFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wzw.ziweishopcity.member.entity.GrowthChangeHistoryEntity;
import com.wzw.ziweishopcity.member.service.GrowthChangeHistoryService;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;



/**
 * 成长值变化历史记录
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 20:55:40
 */
@RestController
@RequestMapping("member/growthchangehistory")
public class GrowthChangeHistoryController {
    @Autowired
    private GrowthChangeHistoryService growthChangeHistoryService;


    @Autowired
    private CouponFeignService couponFeignService;

    /**
     *测试feign
     */
    @RequestMapping("/memberFeignTest")
    public R test(){
        R r = couponFeignService.memberCoupons();
        return r;
    }



    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = growthChangeHistoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		GrowthChangeHistoryEntity growthChangeHistory = growthChangeHistoryService.getById(id);

        return R.ok().put("growthChangeHistory", growthChangeHistory);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody GrowthChangeHistoryEntity growthChangeHistory){
		growthChangeHistoryService.save(growthChangeHistory);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody GrowthChangeHistoryEntity growthChangeHistory){
		growthChangeHistoryService.updateById(growthChangeHistory);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		growthChangeHistoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
