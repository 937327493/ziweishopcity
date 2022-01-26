package com.wzw.ziweishopcity.seckill.conifg;

import com.wzw.ziweishopcity.seckill.interceptor.SeckillInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        SeckillInterceptor seckillInterceptor = new SeckillInterceptor();
        registry.addInterceptor(seckillInterceptor).addPathPatterns("/**");
    }
}
