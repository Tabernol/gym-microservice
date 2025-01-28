package com.krasnopolskyi.config;

import com.krasnopolskyi.int_test.IntegrationTestApplication;
import com.krasnopolskyi.int_test.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(classes = IntegrationTestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
@AutoConfigureMockMvc
@EnableAutoConfiguration
public class CucumberSpringConfiguration {
    @Autowired
    protected TestRestTemplate testRestTemplate;
    @Autowired
    protected JwtService jwtService;
}
