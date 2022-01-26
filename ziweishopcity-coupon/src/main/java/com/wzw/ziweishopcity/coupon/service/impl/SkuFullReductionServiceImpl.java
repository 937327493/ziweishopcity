package com.wzw.ziweishopcity.coupon.service.impl;

import com.wzw.common.to.MemberPrice;
import com.wzw.common.to.SkuReductionTo;
import com.wzw.ziweishopcity.coupon.entity.MemberPriceEntity;
import com.wzw.ziweishopcity.coupon.entity.SkuLadderEntity;
import com.wzw.ziweishopcity.coupon.service.MemberPriceService;
import com.wzw.ziweishopcity.coupon.service.SkuLadderService;
import jdk.nashorn.internal.ir.CallNode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.coupon.dao.SkuFullReductionDao;
import com.wzw.ziweishopcity.coupon.entity.SkuFullReductionEntity;
import com.wzw.ziweishopcity.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    MemberPriceService memberPriceService;
    @Override
    public void savaSkuReduction(SkuReductionTo skuReductionTo) {
        //1、远程调用保存商品spu对应sku的会员优惠、满减、会员价等信息sms_sku_ladder sms_sku_full_reduction sms_member_price
        //（1.1）、sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        skuLadderService.save(skuLadderEntity);
        //（1.2）、sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
        this.save(skuFullReductionEntity);
        //（1.3）、sms_member_price
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map(e -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setMemberPrice(e.getPrice());
            memberPriceEntity.setMemberLevelId(e.getId());
            memberPriceEntity.setMemberLevelName(e.getName());
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

}