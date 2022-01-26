package com.wzw.ziweishopcity.member.exception;

public class UsernameExistException extends RuntimeException{
    public UsernameExistException() {
        super("username已经存在");
    }
}
