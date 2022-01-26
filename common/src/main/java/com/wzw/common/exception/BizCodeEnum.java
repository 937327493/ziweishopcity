package com.wzw.common.exception;

public enum BizCodeEnum {
    Valid_Exception(10001, "格式校验失败"),
    Unknown_Exception(10000, "未知异常"),
    Sms_Exception(12000,"短信获取频繁");
    private Integer code;
    private String message;
    private BizCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
