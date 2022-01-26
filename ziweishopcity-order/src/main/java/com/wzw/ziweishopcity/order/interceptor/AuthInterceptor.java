package com.wzw.ziweishopcity.order.interceptor;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {
    public static final ThreadLocal th = new ThreadLocal();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String requestURI = request.getRequestURI();
        boolean match1 = antPathMatcher.match("/alipayNotify", requestURI);
        if (match1 == true){
            return true;
        }
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
