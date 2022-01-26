package com.wzw.ziweishopcity.member.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
@Data
public class MemberRegisterVo {
    private String username;
    private String password;
    private String phone;
}
