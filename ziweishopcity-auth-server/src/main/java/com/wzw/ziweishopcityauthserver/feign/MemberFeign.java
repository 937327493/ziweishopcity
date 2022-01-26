package com.wzw.ziweishopcityauthserver.feign;

import com.wzw.common.utils.R;
import com.wzw.ziweishopcityauthserver.vo.SocialLoginResultVo;
import com.wzw.ziweishopcityauthserver.vo.UserLoginVo;
import com.wzw.ziweishopcityauthserver.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("ziweishopcity-member")
public interface MemberFeign {
    @PostMapping("/member/member/register")
    public R regist(@RequestBody UserRegistVo userRegistVo);

    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo userLoginVo);

    @PostMapping("/member/member/socialLogin")
    public R socialLogin(@RequestBody SocialLoginResultVo socialLoginResultVo);
}
