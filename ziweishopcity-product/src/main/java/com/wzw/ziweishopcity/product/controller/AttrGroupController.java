package com.wzw.ziweishopcity.product.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wzw.ziweishopcity.product.dao.AttrAttrgroupRelationDao;
import com.wzw.ziweishopcity.product.entity.AttrAttrgroupRelationEntity;
import com.wzw.ziweishopcity.product.entity.AttrEntity;
import com.wzw.ziweishopcity.product.service.AttrAttrgroupRelationService;
import com.wzw.ziweishopcity.product.service.AttrService;
import com.wzw.ziweishopcity.product.service.CategoryService;
import com.wzw.ziweishopcity.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wzw.ziweishopcity.product.entity.AttrGroupEntity;
import com.wzw.ziweishopcity.product.service.AttrGroupService;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;


/**
 * 属性分组
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {


    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    ///product/attrgroup/{catelogId}/withattr要根据商品分类得到其中的所有属性分组+基本属性
    @GetMapping("{catelogId}/withattr")
    public R withattr(@PathVariable("catelogId") Long catelogId) {
        //1、根据catelogId得到对应所有的属性分组
        List<AttrGroupEntity> group =
                attrGroupService.list(new QueryWrapper<AttrGroupEntity>()
                        .eq("catelog_id", catelogId));
        //2、根据每个得到的属性分组，拿出其中的基本属性
        List<BrandGroupAttrRespVo> attr_group_id1 = null;
        if (group != null && group.size() > 0) {
            attr_group_id1 = group.stream().map(e -> {
                //1、将属性分组Po对拷到属性分组Vo,加入集合
                BrandGroupAttrRespVo brandGroupAttrRespVo = new BrandGroupAttrRespVo();
                BeanUtils.copyProperties(e, brandGroupAttrRespVo);
                //2、根据属性分组attrGroupId查询到中间表中对应的基本属性
                List<AttrAttrgroupRelationEntity> attr_group_id = attrAttrgroupRelationService
                        .list(new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_group_id", e.getAttrGroupId()));
                List<BrandGroupAttrInnerRespVo> attrEntityList = null;
                if (attr_group_id != null && attr_group_id.size() > 0) {
                    attrEntityList = attr_group_id.stream().map(c -> {
                        AttrEntity byId = attrService.getById(c.getAttrId());
                        BrandGroupAttrInnerRespVo brandGroupAttrInnerRespVo
                                = new BrandGroupAttrInnerRespVo();
                        BeanUtils.copyProperties(byId, brandGroupAttrInnerRespVo);
                        brandGroupAttrInnerRespVo.setAttrGroupId(c.getAttrGroupId());
                        return brandGroupAttrInnerRespVo;
                    }).collect(Collectors.toList());
                }
                //3、将这些基本属性放进brandGroupAttrRespVo
                if (attrEntityList != null && attrEntityList.size() > 0) {
                    brandGroupAttrRespVo.setAttrs(attrEntityList);
                }
                return brandGroupAttrRespVo;
            }).collect(Collectors.toList());
        }
        return R.ok().put("data",attr_group_id1);
    }

    //attr/relation
    @PostMapping("/attr/relation")
    public R attrRelation(@RequestBody List<AttrGroupRelationVo> list) {
        attrAttrgroupRelationService.attrAttrGroupRelation(list);
        return R.ok();
    }

    ///4/noattr/relation
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noattrRelation(@PathVariable("attrgroupId") Long attrgroupId, @RequestParam Map<String, Object> params) {
        PageUtils pageUtils = attrService.getNoRelationAttr(attrgroupId, params);
        return R.ok().put("page", pageUtils);
    }

    ///product/attrgroup/attr/relation/delete
    @PostMapping("/attr/relation/delete")
    public R attrRelationDelete(@RequestBody AttrGroupRelationVo[]
                                        attrGroupRelationVo) {
        attrAttrgroupRelationService.deleteRelation(attrGroupRelationVo);
        return R.ok();
    }

    ///product/attrgroup/{attrgroupId}/attr/relation
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrAttrGroupRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrRespVo> allRelationAttr = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data", allRelationAttr);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable(value = "catelogId", required = true) Long catId) {
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params, catId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(catelogPath);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
