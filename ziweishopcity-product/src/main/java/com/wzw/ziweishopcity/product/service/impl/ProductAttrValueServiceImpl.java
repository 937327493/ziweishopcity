package com.wzw.ziweishopcity.product.service.impl;

import com.wzw.ziweishopcity.product.entity.AttrEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.product.dao.ProductAttrValueDao;
import com.wzw.ziweishopcity.product.entity.ProductAttrValueEntity;
import com.wzw.ziweishopcity.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Transactional
    @Override
    public void updateAllSpu(Long spuId, List<ProductAttrValueEntity> attrEntities) {
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
        List<ProductAttrValueEntity> collect = attrEntities.stream().map(e -> {
            e.setSpuId(spuId);
            return e;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

    @Override
    public List<ProductAttrValueEntity> getBaseAttrValueList(Long spuId) {
        List<ProductAttrValueEntity> spu_id = this.list(new QueryWrapper<ProductAttrValueEntity>()
                .eq("spu_id", spuId));
        return spu_id;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAttrProducts(List<ProductAttrValueEntity> collect) {
        this.saveBatch(collect);
    }

}