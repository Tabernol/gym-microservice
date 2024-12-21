package com.krasnopolskyi.fitcoach.http.client;

import com.krasnopolskyi.fitcoach.service.JwtService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Autowired
    private JwtService jwtService;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                // Generate the service JWT token here
                String token = jwtService.generateServiceToken(); // 20 seconds
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
