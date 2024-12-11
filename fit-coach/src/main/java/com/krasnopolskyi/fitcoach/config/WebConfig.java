package com.krasnopolskyi.fitcoach.config;

import com.krasnopolskyi.fitcoach.http.interceptor.ControllerLogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final ControllerLogInterceptor controllerLogInterceptor;

    // add custom interceptors for spring configuration
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the logging interceptor for all paths
        registry.addInterceptor(controllerLogInterceptor)
                .addPathPatterns("/**"); // Apply to all endpoints
    }
}
