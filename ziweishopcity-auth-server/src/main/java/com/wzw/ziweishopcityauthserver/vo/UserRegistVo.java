package com.wzw.ziweishopcityauthserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserRegistVo {
    @NotEmpty(message = "用户名不可为空")
    @Length(min = 6,max = 18,message = "用户名长度不合法")
    private String username;
    @NotEmpty(message = "密码不可为空")
    @Length(min = 6,max = 18,message = "密码长度不合法")
    private String password;
    @NotEmpty(message = "验证码不可为空")
    private String code;
    @NotEmpty(message = "手机号不可为空")
    @Pattern(regexp = "^[1][3-9][0-9]{9}$",message = "手机号格式错误")
    private String phone;
}
