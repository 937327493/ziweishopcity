package com.wzw.ziweishopcity.product.controller;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wzw.ziweishopcity.product.entity.BrandEntity;
import com.wzw.ziweishopcity.product.service.BrandService;
import com.wzw.ziweishopcity.product.vo.BrandRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wzw.ziweishopcity.product.entity.CategoryBrandRelationEntity;
import com.wzw.ziweishopcity.product.service.CategoryBrandRelationService;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;


/**
 * 品牌分类关联
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private BrandService brandService;
    /**
     * 根据品牌id查询所有关联的分类
     */
    @GetMapping("/brands/list")
    public R brandsList(@RequestParam("catId") Long catId) {
        QueryWrapper<CategoryBrandRelationEntity> categoryBrandRelationEntityQueryWrapper
                = new QueryWrapper<>();
        categoryBrandRelationEntityQueryWrapper.eq("catelog_id", catId);
        List<CategoryBrandRelationEntity> data =
                categoryBrandRelationService.list(categoryBrandRelationEntityQueryWrapper);
        List<BrandRespVo> collect = null;
        if (data != null && data.size() > 0) {
            collect = data.stream().map(e -> {
                BrandRespVo brandRespVo = new BrandRespVo();
                BrandEntity byId = brandService.getById(e.getBrandId());
                brandRespVo.setBrandId(byId.getBrandId());
                brandRespVo.setBrandName(byId.getName());
                return brandRespVo;
            }).collect(Collectors.toList());
        }
        return R.ok().put("data", collect);
    }

    /**
     * 根据品牌id查询所有关联的分类
     */
    @GetMapping("/catelog/list")
    public R catelogList(@RequestParam("brandId") Long brandId) {
        QueryWrapper<CategoryBrandRelationEntity> categoryBrandRelationEntityQueryWrapper
                = new QueryWrapper<>();
        categoryBrandRelationEntityQueryWrapper.eq("brand_id", brandId);

        List<CategoryBrandRelationEntity> data =
                categoryBrandRelationService.list(categoryBrandRelationEntityQueryWrapper);

        return R.ok().put("data", data);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
