package com.wzw.ziweishopcity.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.wzw.common.to.StockNum;
import com.wzw.ziweishopcity.exception.SkuWareLockException;
import com.wzw.ziweishopcity.ware.vo.SkuAndNumVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wzw.ziweishopcity.ware.entity.WareSkuEntity;
import com.wzw.ziweishopcity.ware.service.WareSkuService;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;


/**
 * 商品库存
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:02:23
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/lock/order")//order服务创建订单需要锁定库存
    public Boolean lockOrder(@RequestBody List<SkuAndNumVo> skuAndNumVos, @RequestParam("orderSn") String orderSn) {
        //1根据order服务的远程调用发来的所库存信息进行库存的锁定
        //2根据库存锁定的结果，向order服务返回处理结果
        try {
            Boolean skuLockResult = wareSkuService.lockOrder(skuAndNumVos, orderSn);
            return skuLockResult;
        } catch (SkuWareLockException e) {
            return false;
        }
    }

    /**
     * 商品模块远程调用我，查询一组sku是不是有库存
     */
    @PostMapping("/hastock")
    public List<StockNum> hasTock(@RequestBody List<Long> skuId) {
        List<StockNum> b = wareSkuService.hasTock(skuId);
        return b;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPageByCondition(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
