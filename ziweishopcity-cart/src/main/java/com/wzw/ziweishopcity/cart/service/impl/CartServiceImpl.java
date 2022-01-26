package com.wzw.ziweishopcity.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.wzw.common.utils.R;
import com.wzw.ziweishopcity.cart.feign.CartProductFeign;
import com.wzw.ziweishopcity.cart.interceptor.CartInterceptor;
import com.wzw.ziweishopcity.cart.service.CartService;
import com.wzw.ziweishopcity.cart.vo.Cart;
import com.wzw.ziweishopcity.cart.vo.CartItem;
import com.wzw.ziweishopcity.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("cartServiceImpl")
public class CartServiceImpl implements CartService {
    @Autowired
    CartProductFeign cartProductFeign;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public List<CartItem> item() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //从redis中获取该用户所有选中的购物项
        List<String> values = redisTemplate.opsForHash().values("ziweishopcity:cart:" + userInfoTo.getUserName());
        List<CartItem> collect = values.stream().map(item -> {
            return JSON.parseObject(item, CartItem.class);
        }).filter(e -> {
            return e.getCheck();
        }).collect(Collectors.toList());
        //远程调用product服务得到所有新的价格
        List<CartItem> finalCollect = collect.stream().map(e -> {
            R info = cartProductFeign.info(e.getSkuId());
            Map skuinfo = (Map) info.get("skuInfo");
            e.setPrice(new BigDecimal(skuinfo.get("price") + ""));
            return e;
        }).collect(Collectors.toList());
        return finalCollect;
    }

    @Override
    public CartItem insertRedisCartItem(UserInfoTo userInfoTo, Long skuId, Integer num) {

        if (userInfoTo.getUserName() != null) {//如果不为空则代表是已登录用户；如果为空，则是临时购物车
            if (redisTemplate.opsForHash().get("ziweishopcity:cart:" + userInfoTo.getUserName(), skuId) == null) {
                CartItem cartItem = new CartItem();//如果redis中没有则该用户对于该商品是第一次添加
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setSkuId(skuId);
                R info = cartProductFeign.info(skuId);
                HashMap<String, Object> cartSkuInfoTo = (HashMap) info.get("skuInfo");
                cartItem.setPrice(new BigDecimal(cartSkuInfoTo.get("price") + ""));//远程调用查询
                cartItem.setImage((String) cartSkuInfoTo.get("skuDefaultImg"));//远程调用查询
                cartItem.setTitle((String) cartSkuInfoTo.get("skuTitle"));//远程调用查询
                R r = cartProductFeign.listSaleAttr(skuId);
                List<String> list = (List<String>) r.get("item");
                cartItem.setSkuAttr(list);
                redisTemplate.opsForHash().put("ziweishopcity:cart:" + userInfoTo.getUserName(), skuId, JSON.toJSONString(cartItem));
                return cartItem;
            } else {
                String s = (String) redisTemplate.opsForHash().get("ziweishopcity:cart:" + userInfoTo.getUserName(), skuId);
                CartItem cartItem1 = JSON.parseObject(s, CartItem.class);
                cartItem1.setCount(cartItem1.getCount() + num);
                redisTemplate.opsForHash().put("ziweishopcity:cart:" + userInfoTo.getUserName(), skuId, JSON.toJSONString(cartItem1));
                return cartItem1;
            }
        } else {
            if (redisTemplate.opsForHash().get("ziweishopcity:cart:" + userInfoTo.getUserKey(), skuId) == null) {
                CartItem cartItem = new CartItem();
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setSkuId(skuId);
                R info = cartProductFeign.info(skuId);
                HashMap<String, Object> cartSkuInfoTo = (HashMap) info.get("skuInfo");
                cartItem.setPrice(new BigDecimal(cartSkuInfoTo.get("price") + ""));//远程调用查询
                cartItem.setImage((String) cartSkuInfoTo.get("skuDefaultImg"));//远程调用查询
                cartItem.setTitle((String) cartSkuInfoTo.get("skuTitle"));//远程调用查询
                R r = cartProductFeign.listSaleAttr(skuId);
                List<String> list = (List<String>) r.get("item");
                cartItem.setSkuAttr(list);
                redisTemplate.opsForHash().put("ziweishopcity:cart:" + userInfoTo.getUserKey(), skuId, JSON.toJSONString(cartItem));
                return cartItem;
            } else {
                String s = (String) redisTemplate.opsForHash().get("ziweishopcity:cart:" + userInfoTo.getUserKey(), skuId);
                CartItem cartItem1 = JSON.parseObject(s, CartItem.class);
                cartItem1.setCount(cartItem1.getCount() + num);
                redisTemplate.opsForHash().put("ziweishopcity:cart:" + userInfoTo.getUserKey(), skuId, JSON.toJSONString(cartItem1));
                return cartItem1;
            }
        }
    }

    @Override
    public Cart listCart(UserInfoTo userInfoTo) {
        if (userInfoTo.getUserName() == null) {
            //说明只是一个临时购物车
            Cart cart = new Cart();
            List<String> tempValues = (List<String>) redisTemplate.opsForHash().values("ziweishopcity:cart:" + userInfoTo.getUserKey());
            ArrayList<CartItem> cartItems = new ArrayList<>();
            for (String value : tempValues) {
                CartItem cartItem = JSON.parseObject(value, CartItem.class);
                cartItems.add(cartItem);
            }
            cart.setItems(cartItems);
            return cart;
        } else {
            //username不为空，说明是一个登录用户，是一个登录购物车
            Cart cart = new Cart();
            //1、我先把临时购物车的购物项都复制到在线购物车的redis中
            List<String> tempValues = (List<String>) redisTemplate.opsForHash().values("ziweishopcity:cart:" + userInfoTo.getUserKey());
            ArrayList<CartItem> cartItems = new ArrayList<>();
            for (String value : tempValues) {
                CartItem cartItem = JSON.parseObject(value, CartItem.class);
                redisTemplate.opsForHash().put("ziweishopcity:cart:" + userInfoTo.getUserName(), cartItem.getSkuId(),
                        JSON.toJSONString(cartItem));
            }
            //2、清空该临时购物车的该用户数据
            redisTemplate.delete("ziweishopcity:cart:" + userInfoTo.getUserKey());
            //3、将该用户的所有购物项放到Cart对象中，并返回给页面进行渲染
            List<String> values = (List<String>) redisTemplate.opsForHash().values("ziweishopcity:cart:" + userInfoTo.getUserName());
            for (String value : values) {
                CartItem cartItem = JSON.parseObject(value, CartItem.class);
                cartItems.add(cartItem);
            }
            cart.setItems(cartItems);
            return cart;
        }
    }

    @Override
    public void checkItem(UserInfoTo userInfoTo, Long skuId, Integer check) {
        if (userInfoTo.getUserName() == null) {
            //是临时购物车的话
            String o = (String) redisTemplate.opsForHash().get("ziweishopcity:cart:" + userInfoTo.getUserKey(), skuId);
            CartItem cartItem = JSON.parseObject(o, CartItem.class);
            cartItem.setCheck((check == 1) ? true : false);
            redisTemplate.opsForHash().put("ziweishopcity:cart:" + userInfoTo.getUserKey(), skuId, JSON.toJSONString(cartItem));
        } else {
            //是在线购物车的话
            String o = (String) redisTemplate.opsForHash().get("ziweishopcity:cart:" + userInfoTo.getUserName(), skuId);
            CartItem cartItem = JSON.parseObject(o, CartItem.class);
            cartItem.setCheck((check == 1) ? true : false);
            redisTemplate.opsForHash().put("ziweishopcity:cart:" + userInfoTo.getUserName(), skuId, JSON.toJSONString(cartItem));
        }
    }

    @Override
    public void countItem(UserInfoTo userInfoTo, Long skuId, Integer num) {
        if (userInfoTo.getUserName() == null) {
            //是临时购物车的话
            String o = (String) redisTemplate.opsForHash().get("ziweishopcity:cart:" + userInfoTo.getUserKey(), skuId);
            CartItem cartItem = JSON.parseObject(o, CartItem.class);
            cartItem.setCount(num);
            redisTemplate.opsForHash().put("ziweishopcity:cart:" + userInfoTo.getUserKey(), skuId, JSON.toJSONString(cartItem));
        } else {
            //是在线购物车的话
            String o = (String) redisTemplate.opsForHash().get("ziweishopcity:cart:" + userInfoTo.getUserName(), skuId);
            CartItem cartItem = JSON.parseObject(o, CartItem.class);
            cartItem.setCount(num);
            redisTemplate.opsForHash().put("ziweishopcity:cart:" + userInfoTo.getUserName(), skuId, JSON.toJSONString(cartItem));
        }
    }

    @Override
    public void deleteItem(UserInfoTo userInfoTo, Long skuId) {
        if (userInfoTo.getUserName() == null) {
            //是临时购物车的话
            redisTemplate.opsForHash().delete("ziweishopcity:cart:" + userInfoTo.getUserKey(), skuId);
        } else {
            //是在线购物车的话
            redisTemplate.opsForHash().delete("ziweishopcity:cart:" + userInfoTo.getUserName(), skuId);
        }
    }


}
