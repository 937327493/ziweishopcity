package com.wzw.ziweishopcity.cart.service;

import com.wzw.ziweishopcity.cart.vo.Cart;
import com.wzw.ziweishopcity.cart.vo.CartItem;
import com.wzw.ziweishopcity.cart.vo.UserInfoTo;

import java.util.List;

public interface CartService {
    List<CartItem> item();

    CartItem insertRedisCartItem(UserInfoTo userInfoTo, Long skuId, Integer num);

    Cart listCart(UserInfoTo userInfoTo);

    void checkItem(UserInfoTo userInfoTo, Long skuId, Integer check);

    void countItem(UserInfoTo userInfoTo, Long skuId, Integer num);

    void deleteItem(UserInfoTo userInfoTo, Long skuId);

}
