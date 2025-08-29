package com.hoangloc.homilux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/login/**", "/oauth2/**",
                "/api/v1/payments/callback",
                "/api/v1/event-types/**",
                "/api/v1/services/**",
                "/api/v1/reviews/**",
                "/api/v1/bookings/**",
                "/v3/api-docs/**",
                "/api/v1/auth/**",
                "/api/v1/files",
                "/api/v1/email/**",
                "/storage/**",
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}