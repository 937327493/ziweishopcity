package com.wzw.ziweishopcity.product.web;

import com.wzw.ziweishopcity.product.entity.CategoryEntity;
import com.wzw.ziweishopcity.product.service.CategoryService;
import com.wzw.ziweishopcity.product.vo.Catelog2WebVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;
    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String,List<Catelog2WebVo>> getCatlogJson(){
        Map<String,List<Catelog2WebVo>> catelog2WebVoList = categoryService.getCategoryRedis();
        return catelog2WebVoList;
    }

    @GetMapping(value = {"/index.html","/"})
    public String index(Model model){
        List<CategoryEntity> categoryEntityList = categoryService.getLevelOne();
        model.addAttribute("categorys",categoryEntityList);
        return "login";
    }


}
