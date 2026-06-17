// package com.app.api.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// public class SecurityConfig {

//     /**
//      * Configure application security to permit Swagger and OpenAPI endpoints.
//      *
//      * @param http the HttpSecurity builder used to customize web security
//      * @return the built SecurityFilterChain
//      * @throws Exception when the security configuration cannot be built
//      */
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers(
//                     "/swagger-ui/**",
//                     "/swagger-ui.html",
//                     "/swagger-ui/index.html",
//                     "/v3/api-docs/**",
//                     "/api-docs/**"
//                 ).permitAll()
//                 .anyRequest().permitAll()
//             )
//             .csrf(csrf -> csrf.disable());

//         return http.build();
//     }
// }
package com.app.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/// CORS handling
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
            .cors(cors -> cors.configurationSource(CorsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable());


        return http.build();
    }
}
