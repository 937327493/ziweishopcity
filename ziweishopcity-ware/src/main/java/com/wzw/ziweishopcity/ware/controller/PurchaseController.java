package com.wzw.ziweishopcity.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wzw.ziweishopcity.ware.vo.DoneVo;
import com.wzw.ziweishopcity.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wzw.ziweishopcity.ware.entity.PurchaseEntity;
import com.wzw.ziweishopcity.ware.service.PurchaseService;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;



/**
 * 采购信息
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:02:23
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 确认完成采购单
     */
    @PostMapping("/done")
    public R done(@RequestBody DoneVo doneVo){
        purchaseService.done(doneVo);
        return R.ok();
    }

    /**
     * 采购员领取采购单
     */
    @PostMapping("/receive")
    public R receive(@RequestBody List<Long> ids){
        purchaseService.receive(ids);
        return R.ok();
    }

    /**
     * 合并采购单
     */
    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo){
        purchaseService.mergePruchese(mergeVo);
        return R.ok();
    }

    /**
     * 合并采购单前查询未领取的采购单
     */
    @RequestMapping("/unreceive/list")
    public R unreceive(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnReceive(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        purchaseService.save(purchase);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
