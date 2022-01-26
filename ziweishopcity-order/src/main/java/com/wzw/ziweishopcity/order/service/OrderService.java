package com.wzw.ziweishopcity.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.order.entity.OrderEntity;
import com.wzw.ziweishopcity.order.vo.*;

import java.util.Map;

/**
 * 订单
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 20:58:42
 */
public interface OrderService extends IService<OrderEntity> {
    void saveSeckillOrderMessage(SeckillOrderVo seckillOrderVo);

    String orderPageShow(Map<String, Object> params);

    PayVo getOrderInfo(String orderSn);

    ResultOrderSubmitVo toSubmitOrder(OrderSubmitVo orderSubmitVo);

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo getOrderConfirmVo();

    void checkOrderStatus(String orderSn);

}

