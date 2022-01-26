package com.wzw.ziweishopcity.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //拿到浏览器发来的老请求参数
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    //得到浏览器发来的老请求
                    HttpServletRequest request = requestAttributes.getRequest();
                    if (request != null) {
                        //通过老请求得到Cookie并放到feign的请求模板的请求头
                        String cookie = request.getHeader("Cookie");
                        requestTemplate.header("Cookie", cookie);
                    }
                }
            }
        };
    }
}
