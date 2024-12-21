package com.krasnopolskyi.gateway.config;

import com.krasnopolskyi.gateway.interceptor.GatewayRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class GatewayWebConfig implements WebMvcConfigurer {

    private final GatewayRequestInterceptor gatewayRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the logging interceptor for all paths
        registry.addInterceptor(gatewayRequestInterceptor)
                .addPathPatterns("/**"); // Apply to all endpoints
    }
}
