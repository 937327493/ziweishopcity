package com.wzw.ziweishopcity.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@EnableRedisHttpSession
@Configuration
public class SessionConfig {
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setDomainName("ziweishopcity.com");
        defaultCookieSerializer.setCookieName("ZIWEISESSION");
        return defaultCookieSerializer;
    }
    @Bean
    public RedisSerializer  redisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
