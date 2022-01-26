package com.wzw.ziweishopcity.product.service.impl;

import com.mysql.cj.util.StringUtils;
import com.wzw.ziweishopcity.product.entity.SpuInfoEntity;
import com.wzw.ziweishopcity.product.service.SpuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.product.dao.SkuInfoDao;
import com.wzw.ziweishopcity.product.entity.SkuInfoEntity;
import com.wzw.ziweishopcity.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SpuInfoService spuInfoService;

    @Override
    public SpuInfoEntity getSpuBySku(Long skuId) {
        SkuInfoEntity skuInfo = this.getById(skuId);
        SpuInfoEntity spuInfo = spuInfoService.getById(skuInfo.getSpuId());
        return spuInfo;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> skuInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isNullOrEmpty(key)) {
            skuInfoEntityQueryWrapper.and(obj -> {
                obj.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isNullOrEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            skuInfoEntityQueryWrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isNullOrEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            skuInfoEntityQueryWrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isNullOrEmpty(min)) {
            skuInfoEntityQueryWrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isNullOrEmpty(max) && !"0".equalsIgnoreCase(max)) {
            skuInfoEntityQueryWrapper.le("price", max);

        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                skuInfoEntityQueryWrapper
        );
        return new PageUtils(page);
    }

}