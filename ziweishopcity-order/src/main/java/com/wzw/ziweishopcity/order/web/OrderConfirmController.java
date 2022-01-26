package com.wzw.ziweishopcity.order.web;

import com.wzw.ziweishopcity.order.exception.SkuWareLockException;
import com.wzw.ziweishopcity.order.service.OrderService;
import com.wzw.ziweishopcity.order.vo.OrderConfirmVo;
import com.wzw.ziweishopcity.order.vo.OrderSubmitVo;
import com.wzw.ziweishopcity.order.vo.ResultOrderSubmitVo;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class OrderConfirmController {
    @Autowired
    OrderService orderService;

    @Autowired
    RocketMQTemplate rocketMQTemplate;
    @GetMapping("/{path}.html")
    public String page(@PathVariable("path") String path) {
        return path;
    }

    @GetMapping("/toConfirm")
    public String toConfirm(Model model) {
        OrderConfirmVo orderConfirmVo = orderService.getOrderConfirmVo();
        model.addAttribute("OrderConfirmData", orderConfirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String toSubmitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes) {
        ResultOrderSubmitVo resultOrderSubmitVo = null;
        try {
            resultOrderSubmitVo =
                    orderService.toSubmitOrder(orderSubmitVo);
        } catch (SkuWareLockException e) {
            System.out.println("库存不够创建订单异常！！！");
            redirectAttributes.addFlashAttribute("msg", "库存不够创建订单异常！！！");
            return "redirect:http://order.ziweishopcity.com/toConfirm";//重定向到这可以自动重新产生幂等性令牌token
        }
        if (resultOrderSubmitVo.getResultCode() == 0) {
            model.addAttribute("ResultOrderSubmitVo", resultOrderSubmitVo);
            rocketMQTemplate.asyncSend("order-delay",
                    MessageBuilder.withPayload(resultOrderSubmitVo.getOrderEntity().getOrderSn()).build(),
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
                    }, 3000, 4);
            return "pay";
        } else {
            String msg = null;
            switch (resultOrderSubmitVo.getResultCode()) {
                case 1:
                    msg = "重复多次提交订单！";
                    break;
                case 2:
                    msg = "未定义！";
                    break;
            }
            redirectAttributes.addFlashAttribute("msg", msg);
            return "redirect:http://order.ziweishopcity.com/toConfirm";//重定向到这可以自动重新产生幂等性令牌token，并展示错误信息
        }
    }
}
