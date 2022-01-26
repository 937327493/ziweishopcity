package com.wzw.ziweishopcity.product.service;

import com.wzw.ziweishopcity.product.vo.SkuItemVo;

import java.util.concurrent.ExecutionException;

public interface ItemService {
    SkuItemVo getSkuItem(Long skuId) throws ExecutionException, InterruptedException;
}
