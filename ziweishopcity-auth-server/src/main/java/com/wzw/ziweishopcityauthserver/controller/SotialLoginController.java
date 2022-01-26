package com.wzw.ziweishopcityauthserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wzw.common.utils.R;
import com.wzw.ziweishopcityauthserver.feign.MemberFeign;
import com.wzw.ziweishopcityauthserver.util.HttpUtils;
import com.wzw.ziweishopcityauthserver.vo.SocialLoginResultVo;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.*;

import java.util.HashMap;
import java.util.Map;


@Controller
public class SotialLoginController {
    @Autowired
    MemberFeign memberFeign;

    @GetMapping("/social/login")
    public String weiBoJieKou(@RequestParam("code") String code, HttpSession session) throws Exception {

        String client_id = "3072760171";
        String client_secret = "87b73ddbcdd8eec93b5ef3f1d420028a";
        String grant_type = "authorization_code";
        String redirect_uri = "http://auth.ziweishopcity.com/social/login";
        HashMap<String, String> body = new HashMap<>();
        body.put("client_id", client_id);
        body.put("client_secret", client_secret);
        body.put("grant_type", grant_type);
        body.put("redirect_uri", redirect_uri);
        body.put("code", code);
        HashMap<String, String> header = new HashMap<>();
        header.put("a", "a");
        HttpResponse post = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token",
                "post", header, null, body);
        String s1 = null;
        SocialLoginResultVo socialLoginResultVo = null;
        int responseCode = post.getStatusLine().getStatusCode();
        if (responseCode == 200) {
            InputStream content = post.getEntity().getContent();
            //转换流
            InputStreamReader inputStreamReader = new InputStreamReader(content);
            //缓冲流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while ((s1 = bufferedReader.readLine()) != null) {
                String s2 = new String(s1.getBytes("utf-8"));
                stringBuffer.append(s2);
            }
            String json = stringBuffer.toString();
            socialLoginResultVo = JSON.parseObject(json, new TypeReference<SocialLoginResultVo>() {
            });
        }
        if (socialLoginResultVo != null) {
            R auth2Result = memberFeign.socialLogin(socialLoginResultVo);
            if ((Integer) auth2Result.get("code") == 0) {
                Map map = (Map) auth2Result.get("socialUser");
                String username = (String) map.get("username");
                session.setAttribute("loginUser", username);
                return "redirect:http://ziweishopcity.com";
            } else
                return "redirect:http://auth.ziweishopcity.com/login.html";
        } else
            return "redirect:http://auth.ziweishopcity.com/login.html";
    }
}
