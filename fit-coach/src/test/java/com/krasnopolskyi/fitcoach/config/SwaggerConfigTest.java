package com.krasnopolskyi.fitcoach.config;

import static org.junit.jupiter.api.Assertions.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigTest {
    private final SwaggerConfig swaggerConfig = new SwaggerConfig();

    @Test
    void customOpenAPI_shouldReturnConfiguredOpenAPI() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Assert
        assertThat(openAPI).isNotNull();

        // Check the Info object
        Info apiInfo = openAPI.getInfo();
        assertThat(apiInfo).isNotNull();
        assertThat(apiInfo.getTitle()).isEqualTo("Gym API");
        assertThat(apiInfo.getDescription()).contains("Trainee:", "Trainer:");
        assertThat(apiInfo.getVersion()).isEqualTo("v0.0.1");

        // Check License information
        License license = apiInfo.getLicense();
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("Created by Maksym Krasnopolskyi");
        assertThat(license.getUrl()).isEqualTo("https://www.linkedin.com/in/maksym-krasnopolskyi-10a245245/");

        // Check security requirements
        assertThat(openAPI.getSecurity()).hasSize(1);
        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        assertThat(securityRequirement.containsKey("bearerAuth")).isTrue();

        // Check security scheme components
        Components components = openAPI.getComponents();
        assertThat(components).isNotNull();
        SecurityScheme securityScheme = components.getSecuritySchemes().get("bearerAuth");
        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(securityScheme.getScheme()).isEqualTo("bearer");
        assertThat(securityScheme.getBearerFormat()).isEqualTo("JWT");
    }
}
