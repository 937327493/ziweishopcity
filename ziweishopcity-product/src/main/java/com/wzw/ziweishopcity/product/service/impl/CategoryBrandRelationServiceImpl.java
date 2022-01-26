package com.wzw.ziweishopcity.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wzw.common.valid.Update;
import com.wzw.ziweishopcity.product.dao.BrandDao;
import com.wzw.ziweishopcity.product.dao.CategoryDao;
import com.wzw.ziweishopcity.product.entity.BrandEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.product.dao.CategoryBrandRelationDao;
import com.wzw.ziweishopcity.product.entity.CategoryBrandRelationEntity;
import com.wzw.ziweishopcity.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;
    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        //1.我们用brandid和categoryid查询出对应的品牌名和商品分类名
        String brandName = brandDao
                .selectById(categoryBrandRelation.getBrandId()).getName();
        String catelogName = categoryDao
                .selectById(categoryBrandRelation.getCatelogId()).getName();
        categoryBrandRelation.setBrandName(brandName);
        categoryBrandRelation.setCatelogName(catelogName);
        categoryBrandRelationDao.insert(categoryBrandRelation);
    }
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }




}