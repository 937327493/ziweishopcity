package com.wzw.ziweishopcity.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 200, 10, TimeUnit.SECONDS
                , new ArrayBlockingQueue(100000), Executors.defaultThreadFactory()
                , new ThreadPoolExecutor.DiscardPolicy());
        return threadPoolExecutor;
    }
}
