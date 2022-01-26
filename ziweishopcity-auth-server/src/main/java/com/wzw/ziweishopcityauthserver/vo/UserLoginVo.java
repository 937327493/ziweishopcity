package com.wzw.ziweishopcityauthserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginVo {
    @NotEmpty(message = "用户名或手机号不可为空")
    @Length(min = 6,max = 18,message = "用户名或手机号长度不合法")
    private String username;
    @NotEmpty(message = "密码不可为空")
    @Length(min = 6,max = 18,message = "密码长度不合法")
    private String password;
}
