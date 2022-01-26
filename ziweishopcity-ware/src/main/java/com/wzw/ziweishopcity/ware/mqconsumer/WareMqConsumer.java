package com.wzw.ziweishopcity.ware.mqconsumer;

import com.wzw.ziweishopcity.ware.service.WareSkuService;
import com.wzw.ziweishopcity.ware.vo.SkuAndNumVoList;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = "ware-consumer", topic = "ware-delay"
        , replyTimeout = 10000, maxReconsumeTimes = -1)//-1表示一直重试消费
public class WareMqConsumer implements RocketMQListener<SkuAndNumVoList> {
    @Autowired
    WareSkuService wareSkuService;

    @Override
    public void onMessage(SkuAndNumVoList skuAndNumVoList) {
        wareSkuService.unlockOrder(skuAndNumVoList.getSkuAndNumUnlockOrder(), skuAndNumVoList.getOrderSn());
    }
}
