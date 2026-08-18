package com.app.api.unit.config;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.app.api.security.FirebaseAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import com.app.api.config.SecurityConfig;

/**
 * Tests for {@link SecurityConfig}.
 * <p>
 * The CORS bean is tested directly with no Spring context. The filter chain
 * behavior (permitAll, CSRF disabled, CORS headers) is verified against a
 * throwaway controller loaded via {@code @WebMvcTest}.
 */
@WebMvcTest(controllers = SecurityConfigTest.PingController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // Real Firebase auth logic isn't under test here — SecurityConfig just needs
    // *a* filter instance to wire in. We mock it and make it a pass-through so
    // requests actually reach the controller.
    @MockitoBean
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    @RestController
    static class PingController {
        @GetMapping("/api/ping")
        public String ping() {
            return "pong";
        }
    }

    @BeforeEach
    void firebaseFilterPassesThrough() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(firebaseAuthenticationFilter).doFilter(any(), any(), any());
    }

    // ---- CORS bean, tested in isolation, no Spring context ----

    @Test
    void corsConfigurationSource_allowsConfiguredOriginMethodsAndCredentials() {
        SecurityConfig config = new SecurityConfig(mock(FirebaseAuthenticationFilter.class));
        UrlBasedCorsConfigurationSource source =
                (UrlBasedCorsConfigurationSource) config.corsConfigurationSource();

        CorsConfiguration corsConfig = source.getCorsConfigurations().get("/**");

        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOrigins()).containsExactly("http://localhost:3000");
        assertThat(corsConfig.getAllowedMethods())
                .containsExactlyInAnyOrder("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        assertThat(corsConfig.getAllowedHeaders()).containsExactly("*");
        assertThat(corsConfig.getAllowCredentials()).isTrue();
    }

    // ---- Filter chain behavior, via a real (mini) Spring context ----

    @Test
    void preflightRequest_fromAllowedOrigin_getsCorsHeaders() throws Exception {
        mockMvc.perform(options("/api/ping")
                        .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    void preflightRequest_fromDisallowedOrigin_isRejected() throws Exception {
        mockMvc.perform(options("/api/ping")
                        .header(HttpHeaders.ORIGIN, "http://evil.example.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRequest_isPermittedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk());
    }

    @Test
    void postRequest_withoutCsrfToken_isNotBlockedByCsrf() throws Exception {
        // /api/ping has no POST mapping, so 404 is expected. The point of this
        // test is that we get 404, NOT 403 — a 403 here would mean CSRF
        // protection kicked in despite csrf().disable().
        mockMvc.perform(post("/api/ping"))
                .andExpect(status().isNotFound());
    }
}