package com.wzw.ziweishopcity.order.web;

import com.alipay.api.AlipayApiException;
import com.wzw.ziweishopcity.order.service.OrderService;
import com.wzw.ziweishopcity.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderPayController {
    @Autowired
    OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;

    @RequestMapping(value = "/orderPay", produces = "text/html")
    @ResponseBody()
    public String orderPay(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo payVo = orderService.getOrderInfo(orderSn);
        if (payVo == null) {
            return "订单支付超时";
        }
        String pay = alipayTemplate.pay(payVo);//支付宝会返回一个页面，是登陆页面
        System.out.println(pay);
        return pay;
    }


}
