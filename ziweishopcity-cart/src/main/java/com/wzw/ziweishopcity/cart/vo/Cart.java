package com.wzw.ziweishopcity.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车信息
 */
public class Cart {
    private List<CartItem> items;
    private Integer countNum;
    private Integer countType;
    private BigDecimal totalAmount;
    private BigDecimal reduce = new BigDecimal("0.00");
    public Integer getCountNum() {
        int num = 0;
        List<CartItem> items = getItems();
        for (CartItem item : items) {
            num += item.getCount();
        }
        return num;
    }


    public Integer getCountType() {
        int num = 0;
        List<CartItem> items = getItems();
        for (CartItem item : items) {
            num += 1;
        }
        return num;
    }


    public BigDecimal getTotalAmount() {
        BigDecimal reduce = getReduce();
        BigDecimal bigDecimal = new BigDecimal("0.00");
        List<CartItem> items = getItems();
        for (CartItem item : items) {
            bigDecimal = bigDecimal.add(item.getTotalPrice());
        }
        bigDecimal = bigDecimal.subtract(reduce);
        return bigDecimal;
    }


    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }



    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
