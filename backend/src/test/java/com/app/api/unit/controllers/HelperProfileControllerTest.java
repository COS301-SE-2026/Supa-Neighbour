package com.app.api.unit.controllers;

import com.app.api.dtos.HelperProfileResponse;
import com.app.api.dtos.ReviewDTO;
import com.app.api.security.FirebaseAuthenticationFilter; // ASSUMPTION: same package guess as last time — adjust if wrong
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.HelperProfileService;
import com.google.firebase.ErrorCode;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import com.app.api.controllers.HelperProfileController;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = HelperProfileController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = FirebaseAuthenticationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class HelperProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HelperProfileService helperProfileService;

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    private static final String VALID_TOKEN = "Bearer valid-token";
    private static final int HELPER_ID = 7;
    private static final int USER_ID = 42;

    private HelperProfileResponse sampleProfile;

    @BeforeEach
    void setUp(){
        List<ReviewDTO> reviews = List.of(
                new ReviewDTO("Great help with the garden", "Excellent", "2026-08-08")
        );

        sampleProfile = new HelperProfileResponse(HELPER_ID,
            "Thabo M.", 
            "Gold",
            4.8, 
            15, 
            9, 
            List.of("Gardening"),
            reviews);
        
    }

    @Test
    void getHelperProfile_validToken_returnsOkWtiProfile() throws Exception{
        FirebaseToken token = mock(FirebaseToken.class);
        when(firebaseAuthService.verifyIdToken("valid-token")).thenReturn(token);
        when(helperProfileService.getProfile(HELPER_ID)).thenReturn(sampleProfile);

        mockMvc.perform(get("/api/helpers/{helperId}/profile", HELPER_ID)
                        .header("Authorization", VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.helperId").value(HELPER_ID))
                .andExpect(jsonPath("$.displayName").value("Thabo M."))
                .andExpect(jsonPath("$.level").value("Gold"))
                .andExpect(jsonPath("$.trustScore").value(4.8))
                .andExpect(jsonPath("$.completedTasks").value(15))
                .andExpect(jsonPath("$.neighboursHelped").value(9))
                .andExpect(jsonPath("$.skills[0]").value("Gardening"))
                .andExpect(jsonPath("$.reviews[0].snippet").exists());

        verify(helperProfileService).getProfile(HELPER_ID);
    }

    @Test
    void getHelperProfile_invalidToken_returns401() throws Exception{
        when(firebaseAuthService.verifyIdToken("bad-token"))
            .thenThrow(new FirebaseAuthException(
                ErrorCode.UNAUTHENTICATED,
                "Invalid token",
                null,
                null,
                AuthErrorCode.INVALID_ID_TOKEN
            ));

        mockMvc.perform(get("/api/helpers/{helperId}/profile", HELPER_ID)
        .header("Authorization", "Bearer bad-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid or expired Firebase Token"));

        verifyNoInteractions(helperProfileService);
    }   

    @Test
    void getHelperProfile_helperNOtFound_return404() throws Exception{
        FirebaseToken token = mock(FirebaseToken.class);
        when(firebaseAuthService.verifyIdToken("valid-token")).thenReturn(token);

        when(helperProfileService.getProfile(HELPER_ID)).thenThrow(
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Helper not found")
        );

        mockMvc.perform(get("/api/helpers/{helperId}/profile", HELPER_ID)
        .header("Authorization", VALID_TOKEN))
        .andExpect(status().isNotFound());
    }

    @Test
    void getHelperProfile_missingAuthHeader_returns400() throws Exception{
        mockMvc.perform(get("/api/helpers/{helperId}/profile", HELPER_ID))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(firebaseAuthService, helperProfileService);
    }

    @Test
    void getHelperProfileByUserId_validToken_returnsOkWithProfile() throws Exception{
        FirebaseToken token = mock(FirebaseToken.class);
        when(firebaseAuthService.verifyIdToken("valid-token")).thenReturn(token);

        when(helperProfileService.getProfileByUserId(USER_ID)).thenReturn(sampleProfile);

        mockMvc.perform(get("/api/helpers/by-user/{userId}/profile", USER_ID)
            .header("Authorization", VALID_TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.helperId").value(HELPER_ID));

        verify(helperProfileService).getProfileByUserId(USER_ID);
    }

    @Test
    void getHelperProfileByUserId_invalidToken_returns401() throws Exception{
        when(firebaseAuthService.verifyIdToken("bad-token"))
            .thenThrow(new FirebaseAuthException(
                ErrorCode.UNAUTHENTICATED,
                "Invalid token",
                null,
                null,
                AuthErrorCode.INVALID_ID_TOKEN
            ));

        mockMvc.perform(get("/api/helpers/by-user/{userId}/profile", USER_ID)
        .header("Authorization", "Bearer bad-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid or expired Firebase Token"));
    }

    @Test
    void getHelperProfileByUserId_noHelperRecord_returns404() throws Exception {
        FirebaseToken token = mock(FirebaseToken.class);
        when(firebaseAuthService.verifyIdToken("valid-token")).thenReturn(token);
        when(helperProfileService.getProfileByUserId(USER_ID))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No helper profile found for this user"));

        mockMvc.perform(get("/api/helpers/by-user/{userId}/profile", USER_ID)
                        .header("Authorization", VALID_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void getHelperProfileByUserId_missingAuthHeader_returns400() throws Exception {
        mockMvc.perform(get("/api/helpers/by-user/{userId}/profile", USER_ID))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(firebaseAuthService, helperProfileService);
    }
}
