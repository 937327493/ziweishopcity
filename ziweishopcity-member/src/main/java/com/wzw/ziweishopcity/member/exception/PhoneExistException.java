package com.wzw.ziweishopcity.member.exception;

public class PhoneExistException extends RuntimeException{
    public PhoneExistException() {
        super("phone已经存在");
    }
}
