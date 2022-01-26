package com.wzw.ziweishopcity.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzw.ziweishopcity.product.entity.ProductAttrValueEntity;
import com.wzw.ziweishopcity.product.service.ProductAttrValueService;
import com.wzw.ziweishopcity.product.vo.AttrRespVo;
import com.wzw.ziweishopcity.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wzw.ziweishopcity.product.entity.AttrEntity;
import com.wzw.ziweishopcity.product.service.AttrService;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;


/**
 * 商品属性
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;

    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrList(@PathVariable("spuId") Long spuId ) {
        List<ProductAttrValueEntity> productAttrValueEntitys = productAttrValueService
                .getBaseAttrValueList(spuId);
        return R.ok().put("data", productAttrValueEntitys);
    }
    /**
     * 修改一个商品的基本属性
     */
    @PostMapping("/update/{spuId}")
    public R spuAttrUpdate(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> ProductAttrValueEntity) {
        productAttrValueService.updateAllSpu(spuId,ProductAttrValueEntity);
        return R.ok();
    }



    //    /product/attr/base/list/{catelogId}
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@PathVariable("catelogId") Long catelogId,
                          @RequestParam Map<String,Object> params,
                          @PathVariable("attrType") String attrType ) {
        PageUtils baseAttrList = attrService.getBaseAttrList(catelogId, params,attrType);

        return R.ok().put("page", baseAttrList);
    }



    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrEntity attr) {
        attrService.updateById(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
