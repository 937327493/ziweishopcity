package com.wzw.ziweishopcity.member.web;

import com.alibaba.fastjson.JSON;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;
import com.wzw.ziweishopcity.member.feign.OrderFeignService;
import com.wzw.ziweishopcity.member.vo.OrderEntityVo;
import com.wzw.ziweishopcity.member.vo.OrderItemEntityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderMemberController {
    @Autowired
    OrderFeignService orderFeignService;
    /**
     * 支付宝回调接口，会转到订单列表页
     * 订单列表页的查询，需要当前页
     * @param pageNum
     * @return
     */
    @GetMapping("/orderMember")
    public String orderMember(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,Model model) {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("page",pageNum.toString());
        String pageUtils = orderFeignService.orderPageShow(stringObjectHashMap);
        PageUtils pageUtils1 = JSON.parseObject(pageUtils, PageUtils.class);
        List<OrderEntityVo> list = (List<OrderEntityVo>) pageUtils1.getList();
        model.addAttribute("order",list);
        return "orderList";
    }
}
