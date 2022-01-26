package com.wzw.ziweishopcity.search.controller;

import com.wzw.ziweishopcity.search.service.SearchParamService;
import com.wzw.ziweishopcity.search.vo.SearchParam;
import com.wzw.ziweishopcity.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class SearchController {
    @Autowired
    SearchParamService searchParamService;
    @RequestMapping("/list.html")
    public String indexSearch(SearchParam searchParam, Model model){
        SearchResult searchResult = searchParamService.search(searchParam);
        model.addAttribute("result",searchResult);
        return "index";
    }
}
