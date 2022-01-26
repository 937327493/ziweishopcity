package com.wzw.ziweishopcity.cart.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserInfoTo {
    private String userName;
    private String userKey;
    private Boolean tempUser = false;//为true标识该请求携带了临时标记——userKey，否则不携带
}
