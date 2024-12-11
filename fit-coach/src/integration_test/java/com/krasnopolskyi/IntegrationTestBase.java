package com.krasnopolskyi;

import com.krasnopolskyi.annotation.IT;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@IT
public abstract class IntegrationTestBase {

    private static final MySQLContainer<?> container =
            new MySQLContainer<>("mysql:8.0");

    @BeforeAll
    static void runContainer() {
        container.start();
    }


    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",() -> container.getJdbcUrl());
        registry.add("spring.datasource.username", () -> container.getUsername());
        registry.add("spring.datasource.password", () -> container.getPassword());
    }
}
