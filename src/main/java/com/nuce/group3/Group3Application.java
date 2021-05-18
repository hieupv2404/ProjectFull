package com.nuce.group3;

import com.nuce.group3.interceptor.AuthenticationAndLoggingInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication()
@EnableAsync
@EnableScheduling
public class Group3Application implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(Group3Application.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationAndLoggingInterceptor());
    }
}
