package com.wzw.ziweishopcity.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.wzw.ziweishopcity.product.entity.CategoryEntity;
import com.wzw.ziweishopcity.product.service.CategoryService;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;


/**
 * 商品三级分类
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 我们要查出数据库中所有商品分类信息，并将其以树型结构存储，这个分类信息有
     * 一级分类二级分类三级分类
     */
    @RequestMapping("/list/tree")
    public R list() {
        //1、在service层查出所有分类信息
        List<CategoryEntity> categoryEntities = categoryService.listWithTree();
        //2、根据分类信息的parent_cid字段把数据装入树形结构，我们需要一个新的类型
        //不要忘记jdk8提供的流计算功能
        //我们获取到了所有一级分类，通过所有的一级分类进行遍历计算，生成一个树结构
        List<CategoryEntity> collect = categoryEntities.stream()
                .filter(e -> e.getParentCid() == 0)
                .map(e ->
                        {
                            e.setChildren(getChildrens(e, categoryEntities));
                            return e;
                        }
                )
                .sorted((a,b) -> {return (a.getSort()==null?0:a.getSort()) - (b.getSort()==null?0:b.getSort());})
                .collect(Collectors.toList());
        for (CategoryEntity categoryEntity : collect) {
            System.out.println(categoryEntity);
        }
        return R.ok().put("categoryEntities", collect);
    }

    //3、我们要为一级分类填充其子分类，遍历一级分类同时遍历所有分类，将父分类是一级分类的添加进一级分类的集合内
    public List<CategoryEntity> getChildrens(CategoryEntity categoryEntity, List<CategoryEntity> categoryEntities) {
        List<CategoryEntity> collect = categoryEntities.stream()
                .filter(e -> e.getParentCid() == categoryEntity.getCatId())
                .map(e -> {
                    e.setChildren(getChildrens(e, categoryEntities));
                    return e;
                })
                .sorted((a,b) -> {return (a.getSort()==null?0:a.getSort()) - (b.getSort()==null?0:b.getSort());})
                .collect(Collectors.toList());
        return collect;
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category) {
        categoryService.save(category);
        redisTemplate.delete("catelogJson");
        return R.ok();
    }
    /**
     * 批量修改修改,主要修改排序
     */
    @PostMapping("/update/sort")
    public R updateBatch(@RequestBody List<CategoryEntity> categoryList) {
        categoryService.updateBatchById(categoryList);
        redisTemplate.delete("catelogJson");
        return R.ok();
    }
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category) {
        categoryService.updateById(category);
        redisTemplate.delete("catelogJson");
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds) {
        categoryService.removeByIds(Arrays.asList(catIds));
        redisTemplate.delete("catelogJson");
        return R.ok();
    }

}
