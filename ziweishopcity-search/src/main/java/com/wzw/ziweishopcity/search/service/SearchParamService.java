package com.wzw.ziweishopcity.search.service;

import com.wzw.ziweishopcity.search.vo.SearchParam;
import com.wzw.ziweishopcity.search.vo.SearchResult;

public interface SearchParamService {
    /**
     *
     * @param searchParam 检索的条件参数
     * @return 检索到的数据
     */
    SearchResult search(SearchParam searchParam);
}
