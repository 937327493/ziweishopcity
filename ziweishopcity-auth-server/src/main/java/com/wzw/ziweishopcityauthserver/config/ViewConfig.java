package com.wzw.ziweishopcityauthserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ViewConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry){
        viewControllerRegistry.addViewController("/login.html")
                .setViewName("login");
        viewControllerRegistry.addViewController("/reg.html")
                .setViewName("reg");
    }
}
