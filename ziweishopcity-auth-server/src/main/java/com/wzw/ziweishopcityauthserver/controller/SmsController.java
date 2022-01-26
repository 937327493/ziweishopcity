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
        //redirectAttributes是模仿了session，利用jsessionid来在重定向时将数据携带会页面
        if (bindingResult.hasErrors()) {
            //注册格式校验出错直接转回注册页
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField,
                            FieldError::getDefaultMessage, (v1, v2) -> v1));//多个相同field会冲突，选中首次出现的field
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://127.0.0.1:9000/reg.html";
        }
        //注册格式没错，进行redis中验证码检查，检查通过后调用member服务进行注册
        String code = userRegistVo.getCode();
        String smsCode = (String) redisTemplate.opsForValue().get("sms:phone:" + userRegistVo.getPhone());
        if (StringUtils.isEmpty(smsCode)) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("code", "请重新申请验证码");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://127.0.0.1:9000/reg.html";
        } else {
            if (smsCode.split("_")[0].equals(code)) {//如果验证码校验通过了
                //先将redis中的验证码删掉
                redisTemplate.delete("sms:phone:" + userRegistVo.getPhone());
                //接着远程调用member
                R result = memberFeign.regist(userRegistVo);
                if ((Integer) result.get("code") == 0) {
                    return "redirect:http://127.0.0.1:9000/login.html";
                } else {
                    HashMap<String, String> errors = new HashMap<>();
                    errors.put("exception", (String) result.get("msg"));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://127.0.0.1:9000/reg.html";
                }
            } else {//如果校验未通过接着回到注册页面
                HashMap<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://127.0.0.1:9000/reg.html";
            }
        }
    }

    @ResponseBody
    @RequestMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {
        //1\接口防刷
        //2\60秒内相同手机号杜绝重发验证码,验证码有效时间10分钟
        String smsCode = (String) redisTemplate.opsForValue().get("sms:phone:" + phone);
        if (!StringUtils.isEmpty(smsCode)) {
            String timeStamp = smsCode.split("_")[1];
            Long l = System.currentTimeMillis() - Long.valueOf(timeStamp);
            if (l < 60000) {
                return R.error(BizCodeEnum.Sms_Exception.getCode(),//12000 短信获取频繁
                        BizCodeEnum.Sms_Exception.getMessage());
            }
        }
        String code = UUID.randomUUID().toString().substring(0, 6);
        redisTemplate.opsForValue().set("sms:phone:"
                + phone, code + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);
        smsFeign.sendCode(phone, code);//不需要向前端页面显示什么结果，没成功重新调即可
        return R.ok();
    }
}
