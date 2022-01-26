package com.wzw.ziweishopcity.member.interceptor;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {
    public static final ThreadLocal th = new ThreadLocal();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //拦截器需要排除一些路径，否则所有请求都被拦截，有很多远程feign请求都无法成功请求了
        String requestURI = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher
                .match("/member/**", requestURI);
        if (match == true)
            return true;
        //1从session拿出，看看有没有对应的用户
        String attribute = (String) request.getSession().getAttribute("loginUser");
        //2有对应的用户就把这个用户名放到ThreadLocal内
        if (attribute != null) {
            th.set(attribute);
            return true;
        } else {
            //3没有对应的用户就重定向到登陆页面
            response.sendRedirect("http://auth.ziweishopcity.com/login.html");
            return false;
        }
    }
}
