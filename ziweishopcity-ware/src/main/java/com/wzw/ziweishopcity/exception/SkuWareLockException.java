package com.wzw.ziweishopcity.exception;

import lombok.Data;

@Data
public class SkuWareLockException extends RuntimeException{
    public SkuWareLockException(Long skuId){
        System.out.println("该商品：" + skuId + "：无法锁定库存");
    }
}
