package com.krasnopolskyi.gateway.config;

import com.krasnopolskyi.gateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor()
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final CheckUsernameFilter checkUsernameFilter;//todo need to refactor this logic

    private static final List<String> FREE_PATHS = Arrays.asList(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api/v1/fit-coach/auth/login",
            "/api/v1/fit-coach/auth/logout",
            "/api/v1/fit-coach/auth/sign-up/**",
            "/api/v1/fit-coach/trainees/create",
            "/api/v1/fit-coach/trainers/create",
            "/api/v1/fit-coach/training-types" // Allow this end-point for creating Front-end part
    );

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(FREE_PATHS.toArray(new String[0])).permitAll()  // Allow access to free paths
                        .anyExchange().authenticated())  // All other paths need to be authenticated
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    // Utility method to check if the request path should bypass
    public static boolean isExcludedPath(String requestPath) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return FREE_PATHS.stream().anyMatch(path -> pathMatcher.match(path, requestPath));
    }

    // CORS Configuration Source
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Allow specific origins,
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT","PATCH", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true); // Allow credentials like cookies
        config.setExposedHeaders(Arrays.asList("Authorization")); // Expose headers to the client

        // Dynamic Origin Support
        config.addAllowedOriginPattern("http://localhost:*");  // Allow any localhost with any port

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
