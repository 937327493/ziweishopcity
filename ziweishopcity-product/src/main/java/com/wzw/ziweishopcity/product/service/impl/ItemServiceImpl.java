package com.wzw.ziweishopcity.product.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzw.ziweishopcity.product.entity.SkuImagesEntity;
import com.wzw.ziweishopcity.product.entity.SkuInfoEntity;
import com.wzw.ziweishopcity.product.entity.SpuInfoDescEntity;
import com.wzw.ziweishopcity.product.feign.SeckillFeignService;
import com.wzw.ziweishopcity.product.service.*;
import com.wzw.ziweishopcity.product.vo.SeckillRedisTo;
import com.wzw.ziweishopcity.product.vo.SkuItemSaleAttrVo;
import com.wzw.ziweishopcity.product.vo.SkuItemVo;
import com.wzw.ziweishopcity.product.vo.SpuItemAttrGroupVo;
import io.netty.util.concurrent.CompleteFuture;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    SeckillFeignService seckillFeignService;

    @Override
    public SkuItemVo getSkuItem(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();
        //由于3、4、5步骤的操作需要第一步的操作支持，我们可以利用异步编排的方法
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //skuinfo信息
            SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
            skuItemVo.setInfos(skuInfo);
            return skuInfo;
        }, threadPoolExecutor);

        CompletableFuture saleFuture = infoFuture.thenAcceptAsync(e -> {
            //sku销售属性及其对应属性值
            List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueService
                    .getSkuItemSaleAttrVos((e.getSpuId()));
            skuItemVo.setSaleAttr(skuItemSaleAttrVos);
        }, threadPoolExecutor);

        CompletableFuture despFuture = infoFuture.thenAcceptAsync(e -> {
            //spu的详情介绍图片
            Long spuId = e.getSpuId();
            SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(spuId);
            skuItemVo.setDesp(spuInfoDesc);
        }, threadPoolExecutor);

        CompletableFuture catalogFuture = infoFuture.thenAcceptAsync(e -> {
            //spu属性分组及对应属性的信息 根据spuid和catalogId
            List<SpuItemAttrGroupVo> spuItemAttrGroupVos =
                    attrGroupService
                            .getGroupAttrBySpuIdAndCatalogId(e.getSpuId()
                                    , e.getCatalogId());
            skuItemVo.setGroupAttrs(spuItemAttrGroupVos);
        }, threadPoolExecutor);

        CompletableFuture skuImageFuture = CompletableFuture.runAsync(() -> {
            //sku图片信息
            List<SkuImagesEntity> skuImages = skuImagesService.getImagesBySkuid(skuId);
            skuItemVo.setImages(skuImages);
        }, threadPoolExecutor);


        CompletableFuture seckillRedisFuture = CompletableFuture.runAsync(() -> {
            //TODO 秒杀商品通过远程调用seckill服务获取详情，参数是skuid
            SeckillRedisTo seckillRedisTo = seckillFeignService.productGetSeckillRedisTo(skuId.toString());
            if (seckillRedisTo != null) {
                skuItemVo.setSeckillRedisTo(seckillRedisTo);
            }
        }, threadPoolExecutor);

        //所有线程都结束才能往下，不然有些线程没执行完，skuItemVo的数据不完整
        CompletableFuture.allOf(infoFuture, saleFuture, despFuture, catalogFuture, skuImageFuture, seckillRedisFuture).get();

        return skuItemVo;
    }
}
