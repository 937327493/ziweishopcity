package com.wzw.ziweishopcity.cart.controller;

import com.wzw.ziweishopcity.cart.interceptor.CartInterceptor;
import com.wzw.ziweishopcity.cart.service.CartService;
import com.wzw.ziweishopcity.cart.vo.Cart;
import com.wzw.ziweishopcity.cart.vo.CartItem;
import com.wzw.ziweishopcity.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CartController {
    @Autowired
    CartService cartService;

    @ResponseBody
    @GetMapping("/item")
    public List<CartItem> item(){
        return cartService.item();
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        UserInfoTo userInfoTo = (UserInfoTo) CartInterceptor.threadLocal.get();
        cartService.deleteItem(userInfoTo,skuId);
        return "redirect:http://cart.ziweishopcity.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num){
        UserInfoTo userInfoTo = (UserInfoTo) CartInterceptor.threadLocal.get();
        cartService.countItem(userInfoTo,skuId,num);
        return "redirect:http://cart.ziweishopcity.com/cart.html";
    }

    @GetMapping("/checkItem")//购物车页面选中购物项
    public String checkItem(@RequestParam("skuId") Long skuId,@RequestParam("check") Integer check){
        UserInfoTo userInfoTo = (UserInfoTo) CartInterceptor.threadLocal.get();
        cartService.checkItem(userInfoTo,skuId,check);
        return "redirect:http://cart.ziweishopcity.com/cart.html";
    }

    @GetMapping("/cart.html")//查看购物车
    public String cartListPage(Model model) {
        UserInfoTo userInfoTo = (UserInfoTo) CartInterceptor.threadLocal.get();
        //需要合并临时购物车和在线购物车
        Cart cart = cartService.listCart(userInfoTo);
        model.addAttribute("cart",cart);
        return "cartList";
    }

    @GetMapping("/addToCart")//加入购物车
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num")Integer num
    , Model model) {
        //我们收到加入购物出的请求，需要判断是临时用户加入还是登陆用户加入
        UserInfoTo userInfoTo = (UserInfoTo) CartInterceptor.threadLocal.get();
        CartItem cartItem = cartService.insertRedisCartItem(userInfoTo,skuId,num);
        model.addAttribute("item",cartItem);
        return "success";
    }
}
