package com.krasnopolskyi.security.integration_tests.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
public class WireMockConfig {


    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockTrainerService() {
        return new WireMockServer(WireMockConfiguration.options().port(8765));
    }
}
