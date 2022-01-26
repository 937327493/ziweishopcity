package com.wzw.ziweishopcity.member.vo;

import lombok.Data;

@Data
public class SocialLoginResultVo {
    private String access_token;
    private String remind_in;
    private String expires_in;
    private String uid;
    private String isRealName;
}
