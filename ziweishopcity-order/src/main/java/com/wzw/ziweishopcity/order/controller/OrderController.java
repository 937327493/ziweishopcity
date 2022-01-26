package com.wzw.ziweishopcity.order.controller;

import java.util.Arrays;
import java.util.Map;

import com.wzw.ziweishopcity.order.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.wzw.ziweishopcity.order.entity.OrderEntity;
import com.wzw.ziweishopcity.order.service.OrderService;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;


/**
 * 订单
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 20:58:42
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     *
     * @param params 这就是分页的数据,返回的是JSON字符串
     * @return
     */
    @RequestMapping("/orderPageShow")
    public String orderPageShow(@RequestBody Map<String, Object> params) {
        String page = orderService.orderPageShow(params);
        return page;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order) {
        orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order) {
        orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
