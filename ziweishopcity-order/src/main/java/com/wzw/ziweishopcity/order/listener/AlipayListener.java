package com.wzw.ziweishopcity.order.listener;

import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wzw.ziweishopcity.order.entity.OrderEntity;
import com.wzw.ziweishopcity.order.entity.PaymentInfoEntity;
import com.wzw.ziweishopcity.order.enume.OrderStatusEnum;
import com.wzw.ziweishopcity.order.service.OrderService;
import com.wzw.ziweishopcity.order.service.PaymentInfoService;
import com.wzw.ziweishopcity.order.vo.PayAsyncVo;
import com.wzw.ziweishopcity.order.web.AlipayTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class AlipayListener {
    @Autowired
    OrderService orderService;
    @Autowired
    PaymentInfoService paymentInfoService;
    @Autowired
    AlipayTemplate alipayTemplate;

    /**
     * @param payAsyncVo 封装的是阿里异步通知发来的数据
     * @return 返回的是操作结果，只有success阿里才会认为成功,其他回复都不认为是成功
     */
    @PostMapping("/alipayNotify")
    @Transactional
    public String alipayNotify(PayAsyncVo payAsyncVo, HttpServletRequest request) {
        try {
            //0先进行验签
            //获取支付宝POST过来反馈信息
            Map<String, String> params = new HashMap<>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }
            boolean verify_result = AlipaySignature.rsaCheckV1(params, alipayTemplate.alipay_public_key, alipayTemplate.getCharset(), "RSA2");
            if (verify_result) {
                System.out.println("验签成功....");
            } else {
                return "error";
            }
            //1将支付宝通知存入oms_payment_info表
            PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
            paymentInfoEntity.setAlipayTradeNo(payAsyncVo.getTrade_no());
            paymentInfoEntity.setCreateTime(payAsyncVo.getNotify_time());
            paymentInfoEntity.setPaymentStatus(payAsyncVo.getTrade_status());
            paymentInfoEntity.setOrderSn(payAsyncVo.getOut_trade_no());
            paymentInfoService.save(paymentInfoEntity);
            //2将订单状态改为已付款1，支付宝那边扣款和增款成功了，才能修改订单状态
            if (payAsyncVo.getTrade_status().equals("TRADE_SUCCESS") || payAsyncVo.getTrade_status().equals("TRADE_FINISHED")) {
                String out_trade_no = payAsyncVo.getOut_trade_no();
                OrderEntity order_sn = orderService.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", out_trade_no));
                order_sn.setStatus(OrderStatusEnum.PAYED.getCode());
                orderService.updateById(order_sn);
            }
        } catch (Exception e) {
            return "error";
        }
        return "success";
    }
}
