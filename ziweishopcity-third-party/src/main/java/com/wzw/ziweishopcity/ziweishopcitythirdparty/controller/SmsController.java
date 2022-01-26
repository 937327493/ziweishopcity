package com.wzw.ziweishopcity.ziweishopcitythirdparty.controller;

import com.wzw.common.utils.R;
import com.wzw.ziweishopcity.ziweishopcitythirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    SmsComponent smsComponent;
    @RequestMapping("/sendcode")
    public R sendCode(@RequestParam("phone")String phone, @RequestParam("code")String code){
        smsComponent.sendCode(phone,code);
        return R.ok();
    }
}
