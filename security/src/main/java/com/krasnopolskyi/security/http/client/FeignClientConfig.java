package com.krasnopolskyi.security.http.client;

import com.krasnopolskyi.security.service.JwtService;
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
                String token = jwtService.generateServiceToken();
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
