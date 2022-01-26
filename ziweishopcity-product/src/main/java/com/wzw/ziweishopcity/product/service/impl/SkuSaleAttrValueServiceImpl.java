package com.wzw.ziweishopcity.product.service.impl;

import com.wzw.ziweishopcity.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.product.dao.SkuSaleAttrValueDao;
import com.wzw.ziweishopcity.product.entity.SkuSaleAttrValueEntity;
import com.wzw.ziweishopcity.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSkuItemSaleAttrVos(Long spuId) {

        SkuSaleAttrValueDao baseMapper = this.getBaseMapper();
        List<SkuItemSaleAttrVo> skuItemSaleAttrVos = baseMapper.getSkuItemSaleAttrVos(spuId);
        return skuItemSaleAttrVos;
    }

    @Override
    public List<String> listSaleAttr(Long skuId) {
        SkuSaleAttrValueDao baseMapper = this.getBaseMapper();
        List<String> listString = baseMapper.listSaleAttr(skuId);
        return listString;
    }

}