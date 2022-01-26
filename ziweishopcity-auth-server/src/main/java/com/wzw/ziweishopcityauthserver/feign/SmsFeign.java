package com.wzw.ziweishopcityauthserver.feign;

import com.wzw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("ziweishopcity-third-party")
public interface SmsFeign {
    @RequestMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone")String phone, @RequestParam("code")String code);
}
