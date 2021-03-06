package com.wzw.ziweishopcityauthserver.controller;

import com.wzw.common.exception.BizCodeEnum;
import com.wzw.common.utils.R;
import com.wzw.ziweishopcityauthserver.feign.MemberFeign;
import com.wzw.ziweishopcityauthserver.feign.SmsFeign;
import com.wzw.ziweishopcityauthserver.vo.UserLoginVo;
import com.wzw.ziweishopcityauthserver.vo.UserRegistVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    SmsFeign smsFeign;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MemberFeign memberFeign;

    @PostMapping("/login")
    public String login(@Valid UserLoginVo userLoginVo, BindingResult bindingResult,
                        RedirectAttributes redirectAttributes, HttpSession session) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream().collect
                    (Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (v1, v2) -> v1));
            redirectAttributes.addFlashAttribute("exception", errors);
            return "redirect:http://auth.ziweishopcity.com/login.html";
        }
        R login = memberFeign.login(userLoginVo);
        if ((Integer) login.get("code") == 0) {
            Map map = (Map) login.get("loginUser");
            String username = (String) map.get("username");
            session.setAttribute("loginUser", username);
            return "redirect:http://ziweishopcity.com";
        } else {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("exception", login.get("msg").toString());
            redirectAttributes.addFlashAttribute("exception", errors);
            return "redirect:http://auth.ziweishopcity.com/login.html";
        }
    }


    @PostMapping("/regist")
    public String rigister(@Valid UserRegistVo userRegistVo, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        //redirectAttributes????????????session?????????jsessionid??????????????????????????????????????????
        if (bindingResult.hasErrors()) {
            //?????????????????????????????????????????????
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField,
                            FieldError::getDefaultMessage, (v1, v2) -> v1));//????????????field?????????????????????????????????field
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://127.0.0.1:9000/reg.html";
        }
        //???????????????????????????redis??????????????????????????????????????????member??????????????????
        String code = userRegistVo.getCode();
        String smsCode = (String) redisTemplate.opsForValue().get("sms:phone:" + userRegistVo.getPhone());
        if (StringUtils.isEmpty(smsCode)) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("code", "????????????????????????");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://127.0.0.1:9000/reg.html";
        } else {
            if (smsCode.split("_")[0].equals(code)) {//??????????????????????????????
                //??????redis?????????????????????
                redisTemplate.delete("sms:phone:" + userRegistVo.getPhone());
                //??????????????????member
                R result = memberFeign.regist(userRegistVo);
                if ((Integer) result.get("code") == 0) {
                    return "redirect:http://127.0.0.1:9000/login.html";
                } else {
                    HashMap<String, String> errors = new HashMap<>();
                    errors.put("exception", (String) result.get("msg"));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://127.0.0.1:9000/reg.html";
                }
            } else {//?????????????????????????????????????????????
                HashMap<String, String> errors = new HashMap<>();
                errors.put("code", "???????????????");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://127.0.0.1:9000/reg.html";
            }
        }
    }

    @ResponseBody
    @RequestMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {
        //1\????????????
        //2\60??????????????????????????????????????????,?????????????????????10??????
        String smsCode = (String) redisTemplate.opsForValue().get("sms:phone:" + phone);
        if (!StringUtils.isEmpty(smsCode)) {
            String timeStamp = smsCode.split("_")[1];
            Long l = System.currentTimeMillis() - Long.valueOf(timeStamp);
            if (l < 60000) {
                return R.error(BizCodeEnum.Sms_Exception.getCode(),//12000 ??????????????????
                        BizCodeEnum.Sms_Exception.getMessage());
            }
        }
        String code = UUID.randomUUID().toString().substring(0, 6);
        redisTemplate.opsForValue().set("sms:phone:"
                + phone, code + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);
        smsFeign.sendCode(phone, code);//?????????????????????????????????????????????????????????????????????
        return R.ok();
    }
}
