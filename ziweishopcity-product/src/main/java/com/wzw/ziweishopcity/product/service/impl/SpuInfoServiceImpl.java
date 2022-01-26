package com.wzw.ziweishopcity.product.service.impl;

import com.mysql.cj.util.StringUtils;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.wzw.common.es.Attrs;
import com.wzw.common.es.SkuEs;
import com.wzw.common.to.SkuReductionTo;
import com.wzw.common.to.SpuBoundTo;
import com.wzw.common.to.StockNum;
import com.wzw.common.utils.R;
import com.wzw.ziweishopcity.product.entity.*;
import com.wzw.ziweishopcity.product.feign.CouponFeignService;
import com.wzw.ziweishopcity.product.feign.SearchFeignService;
import com.wzw.ziweishopcity.product.feign.WareFeignService;
import com.wzw.ziweishopcity.product.service.*;
import com.wzw.ziweishopcity.product.vo.*;
import org.apache.logging.log4j.ThreadContext;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService saveDecriptSpuInfo;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrService attrService;

    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;

    @Override
    public void upToShop(Long spuId) {
        SpuInfoEntity byId = this.getById(spuId);
        Long categoryId = byId.getCatalogId();
        //TODO 根据spu的id查出所有spu关联的所有可以被检索的基本属性
        List<ProductAttrValueEntity> spu_id = productAttrValueService.list
                (new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        //得到所有的基本属性并将其属性id、属性名、属性值都收集起来
        List<Attrs> attrs = spu_id.stream().map(e -> {
            Attrs attrs1 = new Attrs();
            attrs1.setAttrName(e.getAttrName());
            attrs1.setAttrId(e.getAttrId());
            attrs1.setAttrValue(e.getAttrValue());
            return attrs1;
        }).collect(Collectors.toList());

        //1、查出当前要上架的spu对应的所有sku信息
        List<SkuInfoEntity> sku = skuInfoService.list(new QueryWrapper<SkuInfoEntity>()
                .eq("spu_id", spuId));
        ArrayList<Long> longs = new ArrayList<>();
        for (SkuInfoEntity skuInfoEntity : sku) {
            longs.add(skuInfoEntity.getSkuId());
        }
        //TODO 向库存服务发送远程调用查询这些sku是否有库存，为了减少远程调用次数，应该在ziweishopcity-ware模块来查询这些spu对应的所有sku是否有库存
        HashMap<Long, Boolean> objectObjectHashMap = new HashMap<>();
        List<StockNum> r = null;
        try{
            r = wareFeignService.hasTock(longs);
            for (StockNum stockNum : r) {
                objectObjectHashMap.put(stockNum.getSkuId(),stockNum.getHastock());
            }
        }catch (Exception e){
            log.error("远程调用出错");
        }
        //2、遍历得到的sku信息，将她们都封装到SkuEs里面
        List<SkuEs> collect = sku.stream().map(e -> {
            SkuEs skuEs = new SkuEs();
            BeanUtils.copyProperties(e, skuEs);
            skuEs.setCatelogId(categoryId);
            skuEs.setAttrs(attrs);
            skuEs.setSkuPrice(e.getPrice());
            skuEs.setSkuImg(e.getSkuDefaultImg());
            skuEs.setTitle(e.getSkuTitle());
            //TODO 看看这个sku有没有库存
            if (objectObjectHashMap.size() > 0) {
                Boolean aBoolean = objectObjectHashMap.get(e.getSkuId());
                if (aBoolean != null && aBoolean == true)
                    skuEs.setHasStock(true);
            } else
                skuEs.setHasStock(false);
            //TODO 向品牌表查询品牌名称和品牌图片
            BrandEntity byId1 = brandService.getById(skuEs.getBrandId());
            skuEs.setBrandName(byId1.getName());
            skuEs.setBrandImg(byId1.getLogo());
            //TODO 向分类表查询分类的名称
            CategoryEntity byId2 = categoryService.getById(skuEs.getCatelogId());
            skuEs.setCatelogName(byId2.getName());
            return skuEs;
        }).collect(Collectors.toList());
        //TODO 把这些上架数据发送给ziweishopcity-search模块，让其保存在es
        R up = searchFeignService.up(collect);
        Object o =  up.get("code");
        String s = o.toString();
        Integer integer = new Integer(s);
        if(integer.equals(0)){
            //如果远程调用成功则将数据库该spu改为上架状态
            this.baseMapper.updateSpuStatus(spuId,1);
        }
    }

    @Override
    @Transactional
    //TODO 远程调用满减优惠异常会导致全部回滚，要解决这个问题
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //1、保存商品的基本信息pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
        //2、保存商品的spu描述图片pms_spu_info_desc
        List<String> images = spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", images));
        saveDecriptSpuInfo.saveDecriptSpuInfo(spuInfoDescEntity);
        //3、保存商品的spu图片集pms_spu_images
        List<String> images1 = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images1);
        //4、保存商品spu的基本属性及其属性值pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(baseAttr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(baseAttr.getAttrId());
            productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
            productAttrValueEntity.setAttrName(attrService
                    .getById(baseAttr.getAttrId()).getAttrName());
            productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveAttrProducts(collect);
        //5、远程调用保存商品的spu的积分信息 sms_spu_bounds
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        couponFeignService.saveSpuBounds(spuBoundTo);
        //5、保存商品的spu对应的所有sku的信息
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {
                //（5.1）、保存商品spu对应sku的基本信息pms_sku_info
                String defaultImg = null;
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);
                //（5.2）、保存商品spu对应sku的图片信息pms_sku_images
                Long skuId1 = skuInfoEntity.getSkuId();
                //这里保存图片，有一些是空图片是不需要保存数据库的将其删除
                List<SkuImagesEntity> collect1 = item.getImages().stream().map(img -> {
                            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                            skuImagesEntity.setImgUrl(img.getImgUrl());
                            skuImagesEntity.setSkuId(skuId1);
                            skuImagesEntity.setDefaultImg(img.getDefaultImg());
                            return skuImagesEntity;
                        }).filter(imgFilter
                                -> {
                            return !StringUtils.isNullOrEmpty(imgFilter.getImgUrl());
                        })
                        .collect(Collectors.toList());
                skuImagesService.saveBatch(collect1);
                //（5.3）、保存商品spu对应sku的销售属性及其属性值pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> collect2 = attr.stream().map(attrs -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity
                            = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attrs, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId1);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(collect2);
                //（5.4）、远程调用保存商品spu对应sku的会员优惠、满减、会员价等信息sms_sku_ladder sms_sku_full_reduction sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId1);
                //如果没有多买优惠、满减就不要远程调用了
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice()
                        .compareTo(new BigDecimal("0")) == 1) {
                    couponFeignService.saveSkuReduction(skuReductionTo);
                }
            });
        }
    }

    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> spuInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isNullOrEmpty(key)) {
            spuInfoEntityQueryWrapper.and(obj ->
                    obj.eq("id", key).or().like("spu_name", key));
        }
        String status = (String) params.get("status");
        if (!StringUtils.isNullOrEmpty(status)) {
            spuInfoEntityQueryWrapper.eq("publish_status", status);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isNullOrEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            spuInfoEntityQueryWrapper.eq("brand_id", brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isNullOrEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            spuInfoEntityQueryWrapper.eq("catalog_id", catelogId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                spuInfoEntityQueryWrapper
        );
        return new PageUtils(page);
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }


}