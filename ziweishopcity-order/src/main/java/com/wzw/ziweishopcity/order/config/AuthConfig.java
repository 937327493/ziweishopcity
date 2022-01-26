package com.wzw.ziweishopcity.order.config;

import com.wzw.ziweishopcity.order.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Bean;
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
