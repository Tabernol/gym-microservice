package com.krasnopolskyi.fitcoach.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        Info gym_api = new Info().title("Gym API")
                .description("This is a gym app with API endpoints." +
                        "<br><br> Trainee: <br> username= john.doe <br> password= root" +
                        "<br><br> Trainer: <br> username= usain.bolt <br> password= root")
                .version("v0.0.1")
                .license(new License().name("Created by Maksym Krasnopolskyi").url("https://www.linkedin.com/in/maksym-krasnopolskyi-10a245245/"));

        return new OpenAPI()
                .info(gym_api)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")  // Use Bearer scheme
                                                .bearerFormat("JWT")  // Indicate JWT format
                                )
                );
    }
}
