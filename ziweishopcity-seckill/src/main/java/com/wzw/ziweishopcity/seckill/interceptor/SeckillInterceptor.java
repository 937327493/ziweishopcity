package com.wzw.ziweishopcity.seckill.interceptor;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SeckillInterceptor implements HandlerInterceptor {
    public static final ThreadLocal th = new ThreadLocal();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        if (antPathMatcher.match("/seckill/**", requestURI)) {
            HttpSession session = request.getSession();
            String loginUser = (String) session.getAttribute("loginUser");//如果有就是用户名
            if (loginUser == null) {
                //没有的话就是重定向到登录页面
                response.sendRedirect("http://auth.ziweishopcity.com/login.html");
                return false;
            }
            th.set(loginUser);
            return true;//如果已经登录就可以进行秒杀
        }
        return true;//如果是访问其他路径则直接通过
    }
}
