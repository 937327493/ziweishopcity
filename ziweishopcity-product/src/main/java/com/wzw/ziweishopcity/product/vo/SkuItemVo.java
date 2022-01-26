package com.wzw.ziweishopcity.product.vo;

import com.wzw.ziweishopcity.product.entity.SkuImagesEntity;
import com.wzw.ziweishopcity.product.entity.SkuInfoEntity;
import com.wzw.ziweishopcity.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    private SkuInfoEntity infos;//sku信息
    private List<SkuImagesEntity> images;//sku的图片信息
    private SpuInfoDescEntity desp;//spu的介绍图片
    private List<SkuItemSaleAttrVo> saleAttr;//销售属性
    private List<SpuItemAttrGroupVo> groupAttrs;//属性分组和基本属性
    private SeckillRedisTo seckillRedisTo;
}
