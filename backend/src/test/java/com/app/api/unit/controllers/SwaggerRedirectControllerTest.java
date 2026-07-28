package com.app.api.unit.controllers;

import com.app.api.controllers.SwaggerRedirectController;
import com.app.api.repositories.UserRepository;
import com.app.api.services.FirebaseAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SwaggerRedirectController.class)
public class SwaggerRedirectControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FirebaseAuthService fireaseAuthService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    void redirectSwaggerUi_success_1() throws Exception
    {
        mockMvc.perform(get("/swagger-ui")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    @Test
    @WithMockUser
    void redirectSwaggerUi_success_2() throws Exception
    {
        mockMvc.perform(get("/swagger-ui/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/swagger-ui/index.html"));
    }
}
