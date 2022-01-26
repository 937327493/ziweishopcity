package com.wzw.ziweishopcity.cart.config;

import com.wzw.ziweishopcity.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        CartInterceptor cartInterceptor = new CartInterceptor();
        registry.addInterceptor(cartInterceptor).addPathPatterns("/**");//对于任何请求都拦截
    }
}
