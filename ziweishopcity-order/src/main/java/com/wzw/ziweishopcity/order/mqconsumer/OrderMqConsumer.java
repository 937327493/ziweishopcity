package com.wzw.ziweishopcity.order.mqconsumer;

import com.wzw.ziweishopcity.order.service.OrderService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = "order-consumer", topic = "order-delay", replyTimeout = 10000
        , maxReconsumeTimes = -1)//-1表示一直重试消费
public class OrderMqConsumer implements RocketMQListener<String> {
    @Autowired
    OrderService orderService;

    @Override
    public void onMessage(String orderSn) {
        orderService.checkOrderStatus(orderSn);
    }
}
