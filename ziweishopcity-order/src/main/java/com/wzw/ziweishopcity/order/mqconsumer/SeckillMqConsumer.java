package com.wzw.ziweishopcity.order.mqconsumer;

import com.wzw.ziweishopcity.order.service.OrderService;
import com.wzw.ziweishopcity.order.vo.SeckillOrderVo;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = "seckill-consumer-group",topic = "seckill-order",selectorExpression = "*")
public class SeckillMqConsumer implements RocketMQListener<SeckillOrderVo> {
    @Autowired
    OrderService orderService;
    @Override
    public void onMessage(SeckillOrderVo seckillOrderVo) {
        orderService.saveSeckillOrderMessage(seckillOrderVo);
    }
}
