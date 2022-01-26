package com.wzw.ziweishopcity.product.exception;

import com.wzw.common.exception.BizCodeEnum;
import com.wzw.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@Slf4j
@RestControllerAdvice(basePackages = "com.wzw.ziweishopcity.product.controller")
public class ValidException {
    @ExceptionHandler(value = Throwable.class)
    public R handlerException(Throwable e) {
        log.error("出现了问题", e.getMessage(), e.getClass());
        return R.error();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerValidException(MethodArgumentNotValidException e) {
        log.error("数据校验出现了问题", e.getMessage(), e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            stringStringHashMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return R.error(BizCodeEnum.Valid_Exception.getCode(), BizCodeEnum.Valid_Exception.getMessage()).put("data", stringStringHashMap);
    }
}
