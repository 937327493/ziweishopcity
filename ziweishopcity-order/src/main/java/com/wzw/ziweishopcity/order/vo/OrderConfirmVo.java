package com.wzw.ziweishopcity.order.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class OrderConfirmVo {
    private List<MemberReceiveAddress> address;//用户的收货地址
    private List<OrderItemVo> items;//用户选择的所有购物项
    private Integer integration;//用户的积分
    private Map<Long, Boolean> hasStock;//是否有库存
    private String token;//订单防重令牌

    public BigDecimal getPrice() {//订单减免前价格
        BigDecimal bigDecimal = new BigDecimal("0");
        for (OrderItemVo item : items) {
            bigDecimal = bigDecimal.add(item.getTotalPrice());
        }
        return bigDecimal;
    }

    public BigDecimal getFinalPrice() {//订单最终价格
        return getPrice();
    }
}
