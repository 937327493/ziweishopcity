package com.wzw.ziweishopcity.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wzw.ziweishopcity.product.entity.SpuInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wzw.ziweishopcity.product.entity.SkuInfoEntity;
import com.wzw.ziweishopcity.product.service.SkuInfoService;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;


/**
 * sku信息
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 获取Sku全部信息
     * @param skuId
     * @return
     */
    @RequestMapping("/secSkuid")
    public R getSkuInfo(@RequestParam("skuId") Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        String jsonString = JSON.toJSONString(skuInfo);
        return R.ok().put("skuInfo", jsonString);
    }


    @RequestMapping("/spuBySku")
    public SpuInfoEntity spuBySku(@RequestParam("skuId") Long skuId) {
        SpuInfoEntity spuInfoEntity = skuInfoService.getSpuBySku(skuId);
        return spuInfoEntity;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo) {
        skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo) {
        skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds) {
        skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
