//package com.krasnopolskyi.fitcoach.config.security;
//
//import com.krasnopolskyi.fitcoach.config.security.filter.CheckUsernameFilter;
//import com.krasnopolskyi.fitcoach.config.security.filter.JwtAuthenticationFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//
//@Configuration
//@EnableWebSecurity()
//@EnableMethodSecurity()
//@RequiredArgsConstructor()
//public class SecurityConfig {
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final CheckUsernameFilter checkUsernameFilter;
//
//    private static final List<String> FREE_PATHS = Arrays.asList(
//            "/swagger-ui/**",
//            "/v3/api-docs/**",
//            "/swagger-resources/**",
//            "/api/v1/fit-coach/authn/login",
//            "/api/v1/fit-coach/trainees/create",
//            "/api/v1/fit-coach/trainers/create",
//            "/api/v1/fit-coach/training-types" // Allow this end-point for creating Front-end part
//    );
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(withDefaults()) // Enable CORS based on a CorsConfigurationSource bean
//                .authorizeHttpRequests(request -> request
//                        .requestMatchers(FREE_PATHS.toArray(new String[0]))
//                        .permitAll()  // Permit all for specified paths
//                        .anyRequest().authenticated())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterAfter(checkUsernameFilter, JwtAuthenticationFilter.class); // Add CheckUsernameFilter after JWT filter
//        return http.build();
//    }
//
//    // Utility method to check if the request path should bypass
//    public static boolean isExcludedPath(String requestPath) {
//        AntPathMatcher pathMatcher = new AntPathMatcher();
//        return FREE_PATHS.stream().anyMatch(path -> pathMatcher.match(path, requestPath));
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
//            throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    // CORS Configuration Source
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Allow specific origins,
//        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT","PATCH", "DELETE"));
//        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//        config.setAllowCredentials(true); // Allow credentials like cookies
//        config.setExposedHeaders(Arrays.asList("Authorization")); // Expose headers to the client
//
//        // Dynamic Origin Support
//        config.addAllowedOriginPattern("http://localhost:*");  // Allow any localhost with any port
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
//}
