package com.app.api.unit.controllers;

import com.app.api.controllers.GoogleCalenderController;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.GoogleCalenderTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = GoogleCalenderController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = FirebaseAuthenticationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class GoogleCalenderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoogleCalenderTokenService tokenService;

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    @Test
    void connect_blankAuthCode_returnsBadRequest() throws Exception {
        when(firebaseAuthService.getUserIdFromToken("valid-token")).thenReturn(42);

        mockMvc.perform(post("/api/users/me/google-calender/connect")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"authCode\":\"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Google authorization code is required"));

        verifyNoInteractions(tokenService);
    }
}
