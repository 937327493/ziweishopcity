package com.wzw.ziweishopcity.ware.service.impl;

import com.wzw.common.constant.WareConstant;
import com.wzw.ziweishopcity.ware.entity.PurchaseDetailEntity;
import com.wzw.ziweishopcity.ware.entity.WareSkuEntity;
import com.wzw.ziweishopcity.ware.service.WareSkuService;
import com.wzw.ziweishopcity.ware.vo.DoneVo;
import com.wzw.ziweishopcity.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.ware.dao.PurchaseDao;
import com.wzw.ziweishopcity.ware.entity.PurchaseEntity;
import com.wzw.ziweishopcity.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    PurchaseDetailServiceImpl purchaseDetailService;
    @Autowired
    WareSkuService wareSkuService;

    @Override
    @Transactional
    public void done(DoneVo doneVo) {

        //1、标志位，如果采购项都成功，则为true
        AtomicBoolean flag = new AtomicBoolean(true);
        //2、遍历采购需求，更新采购需求状态,将采购物品入库
        List<PurchaseDetailEntity> collect = doneVo.getItems().stream().map(e -> {
            Long itemId = e.getItemId();
            PurchaseDetailEntity byId = null;
            if (itemId != null && itemId != 0) {
                byId = purchaseDetailService.getById(itemId);
                if (WareConstant.PurchaseDetailConstant.FINISH.getCode() == e.getStatus()) {
                    byId.setStatus(WareConstant.PurchaseDetailConstant.FINISH.getCode());
                    WareSkuEntity wareSkuEntity = new WareSkuEntity();
                    Long skuId = byId.getSkuId();
                    Integer skuNum = byId.getSkuNum();
                    Long wareId = byId.getWareId();
                    wareSkuEntity.setSkuId(skuId);
                    wareSkuEntity.setWareId(wareId);
                    wareSkuEntity.setStock(skuNum);
                    WareSkuEntity sku_id = wareSkuService.getOne(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId));
                    if (sku_id == null) {
                        wareSkuService.save(wareSkuEntity);
                    } else {
                        sku_id.setStock(skuNum + sku_id.getStock());
                        sku_id.setWareId(wareId);
                        sku_id.setSkuId(skuId);
                        wareSkuService.updateById(sku_id);
                    }
                } else {
                    byId.setStatus(WareConstant.PurchaseDetailConstant.HASERROT.getCode());
                    flag.set(false);
                }
                return byId;
            }
            return byId;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        //3、根据采购需求的采购情况来更新采购单状态
        Long id = doneVo.getId();
        if (id != null && id != 0) {
            PurchaseEntity byId = this.getById(id);
            if (flag.get() == true) {
                byId.setStatus(WareConstant.PurchaseConstant.FINISH.getCode());
                byId.setUpdateTime(new Date());
            } else
                byId.setStatus(WareConstant.PurchaseConstant.HASERROT.getCode());
            this.updateById(byId);
        }
    }

    @Override
    public void receive(List<Long> ids) {
        //1、确定自己的采购单是新建或者已分配状态
        List<PurchaseEntity> collect = ids.stream().map(e -> {
            PurchaseEntity byId = this.getById(e);
            return byId;
        }).filter(f -> {
            if (f.getStatus() == WareConstant.PurchaseConstant.CREATED.getCode() ||
                    f.getStatus() == WareConstant.PurchaseConstant.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        //2、改变采购单的状态
        List<PurchaseEntity> collect1 = collect.stream().map(g -> {
            g.setStatus(WareConstant.PurchaseConstant.RECEIVED.getCode());
            return g;
        }).collect(Collectors.toList());
        this.updateBatchById(collect1);
        //3、改变采购单相关的采购需求的状态
        purchaseDetailService.ListDetailByPurchaseId(ids);
    }


    @Override
    @Transactional
    public void mergePruchese(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseConstant.CREATED.getCode());//实际上状态码可以在common模块写成枚举
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        Long finalPurchaseId = purchaseId;
        List<Long> items = mergeVo.getItems();
        List<PurchaseDetailEntity> collect = items.stream().map(e -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(e);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailConstant.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnReceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status", "0").or().eq("status", "1")
        );
        return new PageUtils(page);
    }


}