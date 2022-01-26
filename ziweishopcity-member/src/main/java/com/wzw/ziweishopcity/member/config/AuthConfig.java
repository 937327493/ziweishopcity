package com.wzw.ziweishopcity.member.config;

import com.wzw.ziweishopcity.member.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        AuthInterceptor authInterceptor = new AuthInterceptor();
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
    }
}
