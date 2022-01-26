package com.wzw.ziweishopcity.cart.interceptor;

import com.wzw.ziweishopcity.cart.vo.UserInfoTo;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser != null) {//如果redis里面缓存了该客户的登录信息，则把该登录信息封装到UserInfoTo内
            userInfoTo.setUserName(loginUser);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {//不论redis内没有缓存该信息,则从请求中携带的cookie找到对应的userKey,然后封装到
            //UserInfoTo内，有的话
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase("userKey")) {
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);//标识该请求头的cookie中拥有标识userKey
                }
            }
        }
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            //如果客户端请求没有发来userKey，则在后台生成一个userKey发送回浏览器去
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        threadLocal.set(userInfoTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        if (userInfoTo.getTempUser() == false) {
            Cookie userKey = new Cookie("userKey", userInfoTo.getUserKey());
            userKey.setDomain("ziweishopcity.com");
            userKey.setMaxAge(60 * 60 * 24 * 30);
            response.addCookie(userKey);
        }
    }
}
