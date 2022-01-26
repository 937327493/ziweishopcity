package com.wzw.ziweishopcity.product.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.product.dao.BrandDao;
import com.wzw.ziweishopcity.product.entity.BrandEntity;
import com.wzw.ziweishopcity.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key1 = (String) params.get("key");
        QueryWrapper<BrandEntity> key = new QueryWrapper<BrandEntity>();
        if (key1 != null) {
            key.like("name", key1)
                    .or()
                    .eq("brand_id", key1);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params), key
        );

        return new PageUtils(page);
    }

}