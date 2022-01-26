package com.wzw.ziweishopcity.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.product.entity.CategoryEntity;
import com.wzw.ziweishopcity.product.vo.Catelog2WebVo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
public interface CategoryService extends IService<CategoryEntity> {
    Map<String,List<Catelog2WebVo>> getCategoryRedis();

    Map<String,List<Catelog2WebVo>> getCatlogJson();

    List<CategoryEntity> getLevelOne();

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    Long[] findCatelogPath(Long catelogId);



}

