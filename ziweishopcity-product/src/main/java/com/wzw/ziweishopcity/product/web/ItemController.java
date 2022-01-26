package com.wzw.ziweishopcity.product.web;

import com.wzw.ziweishopcity.product.service.ItemService;
import com.wzw.ziweishopcity.product.vo.SkuItemSaleAttrVo;
import com.wzw.ziweishopcity.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {
    @Autowired
    ItemService itemService;
    @RequestMapping("/{skuId}.html")
    public String toItenIndex(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = itemService.getSkuItem(skuId);
        model.addAttribute("item",skuItemVo);
        return "item";
    }
}
