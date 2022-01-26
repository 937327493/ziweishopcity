package com.wzw.ziweishopcity.ware.service.impl;

import com.mysql.cj.util.StringUtils;
import com.wzw.common.to.StockNum;
import com.wzw.ziweishopcity.exception.SkuWareLockException;
import com.wzw.ziweishopcity.ware.entity.WareOrderTaskDetailEntity;
import com.wzw.ziweishopcity.ware.entity.WareOrderTaskEntity;
import com.wzw.ziweishopcity.ware.service.WareOrderTaskDetailService;
import com.wzw.ziweishopcity.ware.service.WareOrderTaskService;
import com.wzw.ziweishopcity.ware.vo.SkuAndNumVo;
import com.wzw.ziweishopcity.ware.vo.SkuAndNumVoList;
import com.wzw.ziweishopcity.ware.vo.SkuAndWareAndNumVo;
import io.swagger.models.auth.In;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.ware.dao.WareSkuDao;
import com.wzw.ziweishopcity.ware.entity.WareSkuEntity;
import com.wzw.ziweishopcity.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    WareOrderTaskService wareOrderTaskService;
    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Transactional
    @Override
    public Boolean lockOrder(List<SkuAndNumVo> skuAndNumVos, String orderSn) {
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setTaskStatus(1);//1代表已锁定，创建库存任务就是为了锁定库存
        wareOrderTaskEntity.setOrderSn(orderSn);
        try {
            wareOrderTaskService.save(wareOrderTaskEntity);//如果存储订单任务存储异常，直接返回false即可
        } catch (Exception e) {
            return false;
        }
        //1遍历SkuAndNumVo集合，得到每个商品在哪些仓库中有库存，得到仓库的wareId
        List<SkuAndWareAndNumVo> skuAndWareAndNumVos = new ArrayList<>();
        for (SkuAndNumVo skuAndNumVo : skuAndNumVos) {
            List<Long> wareIds = this.baseMapper.getWareIds(skuAndNumVo.getSkuId());
            if (wareIds != null && wareIds.size() > 0) {//每一个sku商品都需要有库存，任何一个没有库存都视为订单无法创建返回false
                SkuAndWareAndNumVo skuAndWareAndNumVo = new SkuAndWareAndNumVo();
                skuAndWareAndNumVo.setSkuId(skuAndNumVo.getSkuId());//商品skuId
                skuAndWareAndNumVo.setWareId(wareIds);//仓库集合
                skuAndWareAndNumVo.setNum(skuAndNumVo.getNum());//锁定的数量
                skuAndWareAndNumVos.add(skuAndWareAndNumVo);
            } else
                return false;
        }
        //2遍历SkuAndWareAndNumVo，哪个仓库可以支持该sku的库存锁定，支持的话就直接锁定，任意一个商品无法锁定则整个订单不能创建
        for (SkuAndWareAndNumVo skuAndWareAndNumVo : skuAndWareAndNumVos) {
            Boolean lockItemResult = false;//针对每个sku都有一个标志
            for (Long wareId : skuAndWareAndNumVo.getWareId()) {
                Integer numResult = this.baseMapper
                        .lockStock(skuAndWareAndNumVo.getNum(), skuAndWareAndNumVo.getSkuId(), wareId);//符合where条件的行数
                if (numResult == 1) {//表示该仓库锁定成功该商品，符合where条件的行数是一
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
                    wareOrderTaskDetailEntity.setWareId(wareId);
                    wareOrderTaskDetailEntity.setSkuId(skuAndWareAndNumVo.getSkuId());
                    wareOrderTaskDetailEntity.setTaskId(wareOrderTaskEntity.getId());
                    wareOrderTaskDetailEntity.setLockStatus(1);
                    wareOrderTaskDetailEntity.setSkuNum(skuAndWareAndNumVo.getNum());
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    lockItemResult = true;
                    break;
                } else {//表示该仓库无法锁定该商品
                    continue;
                }
            }
            if (lockItemResult == false) {
                throw new SkuWareLockException(skuAndWareAndNumVo.getSkuId());
            }
        }
        SkuAndNumVoList skuAndNumVoList = new SkuAndNumVoList();//设置异常情况下库存自动解锁
        skuAndNumVoList.setSkuAndNumUnlockOrder(skuAndNumVos);
        skuAndNumVoList.setOrderSn(orderSn);
        rocketMQTemplate.asyncSend("ware-delay",
                MessageBuilder.withPayload(skuAndNumVoList).build(),
                new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.println(sendResult.toString());
                        System.out.println("消息队列创建订单成功");
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                        System.out.println("消息队列创建订单异常");
                    }
                }, 3000, 5);//延迟等级5，代表1分钟延迟消息
        return true;//如果锁定库存操作全部完成，没有任何异常，则返回true
    }

    @Override
    public List<StockNum> hasTock(List<Long> skuId) {
        List<StockNum> stockNumList = new ArrayList<StockNum>();
        for (Long aLong : skuId) {
            Long stock = this.baseMapper.getStock(aLong);
            StockNum stockNum = new StockNum();
            if (stock != null && stock > 0) {
                stockNum.setSkuId(aLong);
                stockNum.setHastock(true);//有存货
            } else {
                stockNum.setSkuId(aLong);
                stockNum.setHastock(false);//无存货
            }
            stockNumList.add(stockNum);
        }
        return stockNumList;
    }

    @Override
    @Transactional
    public void unlockOrder(List<SkuAndNumVo> skuAndNumUnlockOrder, String orderSn) {
        for (SkuAndNumVo skuAndNumVo : skuAndNumUnlockOrder) {
            WareOrderTaskEntity wareOrderTask = wareOrderTaskService
                    .getOne(new QueryWrapper<WareOrderTaskEntity>()
                            .eq("order_sn", orderSn));
            //如果锁定库存任务状态不是1，即非锁定状态，则直接返回,不必进行解锁
            if (wareOrderTask.getTaskStatus() != 1) {
                System.out.println("锁定库存任务状态不是1，即非锁定状态，则直接异常该方法,回滚,不必进行解锁");
                return;
            }
            wareOrderTask.setTaskStatus(2);
            wareOrderTaskService.updateById(wareOrderTask);//更新锁库存任务的状态
            WareOrderTaskDetailEntity wareOrderTaskDetail = wareOrderTaskDetailService
                    .getOne(new QueryWrapper<WareOrderTaskDetailEntity>()
                            .eq("task_id", wareOrderTask.getId())
                            .eq("sku_id", skuAndNumVo.getSkuId()));
            wareOrderTaskDetail.setLockStatus(2);//更新锁库存任务详情的状态
            wareOrderTaskDetailService.updateById(wareOrderTaskDetail);
            this.baseMapper.unlockOrder(skuAndNumVo.getSkuId(), skuAndNumVo.getNum(), wareOrderTaskDetail.getWareId());
        }
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isNullOrEmpty(skuId)) {
            wareSkuEntityQueryWrapper.eq("sku_id", skuId);
        }
        if (!StringUtils.isNullOrEmpty(wareId)) {
            wareSkuEntityQueryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wareSkuEntityQueryWrapper
        );
        return new PageUtils(page);
    }
}