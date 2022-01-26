package com.wzw.ziweishopcity.ware.service.impl;

import com.mysql.cj.util.StringUtils;
import com.wzw.common.constant.WareConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.ware.dao.PurchaseDetailDao;
import com.wzw.ziweishopcity.ware.entity.PurchaseDetailEntity;
import com.wzw.ziweishopcity.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    public void ListDetailByPurchaseId(List<Long> ids) {
        List<PurchaseDetailEntity> purchase_id1 = ids.stream().flatMap(a -> {
            Stream<PurchaseDetailEntity> purchase_id = this.list
                            (new QueryWrapper<PurchaseDetailEntity>()
                                    .eq("purchase_id", a))
                    .stream()
                    .map(b -> {
                        PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                        BeanUtils.copyProperties(b, purchaseDetailEntity);
                        purchaseDetailEntity
                                .setStatus(WareConstant.PurchaseDetailConstant
                                        .BUYING.getCode());
                        return purchaseDetailEntity;
                    });
            return purchase_id;
        }).collect(Collectors.toList());
        this.updateBatchById(purchase_id1);

    }
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> purchaseDetailEntityQueryWrapper = new QueryWrapper<>();
        String status = (String) params.get("status");
        String key = (String) params.get("key");
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isNullOrEmpty(status)) {
            purchaseDetailEntityQueryWrapper.eq("status", status);
        }
        if (!StringUtils.isNullOrEmpty(key)) {
            purchaseDetailEntityQueryWrapper.and(obj -> {
                obj.eq("sku_id", key).or().eq("purchase_id", key);
            });
        }
        if (!StringUtils.isNullOrEmpty(wareId)) {
            purchaseDetailEntityQueryWrapper.eq("ware_id", wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                purchaseDetailEntityQueryWrapper
        );
        return new PageUtils(page);
    }


}