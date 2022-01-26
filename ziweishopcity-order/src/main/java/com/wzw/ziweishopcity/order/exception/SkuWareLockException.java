package com.wzw.ziweishopcity.order.exception;

import com.wzw.ziweishopcity.order.vo.ResultOrderSubmitVo;

public class SkuWareLockException extends RuntimeException {
    public ResultOrderSubmitVo resultOrderSubmitVo = null;
    public SkuWareLockException(ResultOrderSubmitVo resultOrderSubmitVo) {
        this.resultOrderSubmitVo = resultOrderSubmitVo;
    }
}
