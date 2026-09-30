package com.app.api.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/// CORS handling
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.app.api.security.FirebaseAuthenticationFilter;


/**
 * Security and CORS configuration for the API.
 */
@Configuration
public class SecurityConfig {

    /**
     * Filter responsible for authenticating requests using Firebase ID tokens.
     */
    private final FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    /**
     * Constructs a new security configuration.
     *
     * @param firebaseAuthenticationFilter the Firebase authentication filter
     *                                     used to validate incoming requests
     */
    public SecurityConfig(FirebaseAuthenticationFilter firebaseAuthenticationFilter) {
        this.firebaseAuthenticationFilter = firebaseAuthenticationFilter;
    }

    /**
     * Configure application security and CORS.
     *
     * @param http the HttpSecurity builder
     * @return the built SecurityFilterChain
     * @throws Exception when the security configuration cannot be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("USING MY SECURITY CONFIG");    
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/register", "/api/auth/login","/v3/api-docs/**").permitAll()
                .requestMatchers("/admin/login").permitAll()
                .anyRequest().permitAll()
            )
            .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            System.out.println("Building SecurityFilterChain");
            return http.build();
    }

    /**
     * Allow requests from the Flutter web dev server.
     *
     * @return the CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "https://red-rock-009e74b03.3.azurestaticapps.net"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
 
