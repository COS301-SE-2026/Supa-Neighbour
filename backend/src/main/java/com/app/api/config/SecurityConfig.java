package com.app.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/// CORS handling
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;



@Configuration
public class SecurityConfig {

    /**
     * Configure application security to permit Swagger and OpenAPI endpoints.
     *
     * @param http the HttpSecurity builder used to customize web security
     * @return the built SecurityFilterChain
     * @throws Exception when the security configuration cannot be built
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
            .cors(cors -> cors.configurationSource(CorsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-ui/index.html",
                    "/v3/api-docs/**",
                    "/api-docs/**"
                ).permitAll()
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable());


        return http.build();
    }



    /**
     * Allow requests from our flutter web server (CORS)
     * 
     * @return the  CORS config source
     */
    @Bean
    public CorsConfigurationSource CorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;

    }
}
