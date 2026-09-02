package com.app.api.unit.controllers;

import com.app.api.controllers.UserProfileController;
import com.app.api.dtos.UpdateProfileRequest;
import com.app.api.dtos.UpdateProfileResponse;
import com.app.api.dtos.UserProfileResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock private UserProfileService userProfileService;
    @Mock private FirebaseAuthService firebaseAuthService;

    @InjectMocks
    private UserProfileController userProfileController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String BEARER = "Bearer valid-token";
    private static final String RAW = "valid-token";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private String updateRequestJson(String firstName, String lastName, List<String> skills) throws Exception {
        Map<String, Object> body = new HashMap<>();
        if (firstName != null) {
            body.put("firstName", firstName);
        }
        if (lastName != null) {
            body.put("lastName", lastName);
        }
        if (skills != null) {
            body.put("skills", skills);
        }
        return objectMapper.writeValueAsString(body);
    }

    // ---------- GET /api/users/me/profile ----------

    @Test
    void getMyProfile_WhenTokenValid_ReturnsProfile() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(1);
        UserProfileResponse profile = new UserProfileResponse(
                1, "John S.", "Hillcrest", "Gold", 500,
                List.of("Pet Care"), 5, List.of(), List.of(), 4.5, 2, 3);
        when(userProfileService.getProfile(1)).thenReturn(profile);

        mockMvc.perform(get("/api/users/me/profile")
                .header("Authorization", BEARER)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.displayName").value("John S."));

        verify(userProfileService).getProfile(1);
    }

    @Test
    void getMyProfile_WhenTokenInvalid_ReturnsUnauthorized() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(get("/api/users/me/profile")
                .header("Authorization", BEARER))
                .andExpect(status().isUnauthorized());

        verify(userProfileService, never()).getProfile(anyInt());
    }

    @Test
    void getMyProfile_WhenUserNotFound_ReturnsNotFound() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(99);
        when(userProfileService.getProfile(99))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        mockMvc.perform(get("/api/users/me/profile")
                .header("Authorization", BEARER))
                .andExpect(status().isNotFound());
    }

    // ---------- PATCH /api/users/me/profile ----------

    @Test
    void updateProfile_WhenTokenValid_ReturnsUpdatedProfile() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(1);
        UpdateProfileResponse res = new UpdateProfileResponse("Profile updated", "Jane S.", null);
        when(userProfileService.updateProfile(eq(1), any(UpdateProfileRequest.class))).thenReturn(res);

        mockMvc.perform(patch("/api/users/me/profile")
                .header("Authorization", BEARER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson("Jane", null, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated"))
                .andExpect(jsonPath("$.displayName").value("Jane S."));

        verify(userProfileService).updateProfile(eq(1), any(UpdateProfileRequest.class));
    }

    @Test
    void updateProfile_WhenTokenInvalid_ReturnsUnauthorized() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(patch("/api/users/me/profile")
                .header("Authorization", BEARER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson("Jane", null, null)))
                .andExpect(status().isUnauthorized());

        verify(userProfileService, never()).updateProfile(anyInt(), any());
    }

    @Test
    void updateProfile_WhenRequestIsEmpty_ReturnsUnprocessableEntity() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(1);
        when(userProfileService.updateProfile(eq(1), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "At least one field required"));

        mockMvc.perform(patch("/api/users/me/profile")
                .header("Authorization", BEARER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson(null, null, null)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateProfile_WhenSkillsUpdated_ReturnsUpdatedSkills() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(1);
        UpdateProfileResponse res = new UpdateProfileResponse("Profile updated", "John S.", List.of("Pet Care"));
        when(userProfileService.updateProfile(eq(1), any())).thenReturn(res);

        mockMvc.perform(patch("/api/users/me/profile")
                .header("Authorization", BEARER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson(null, null, List.of("Pet Care"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills[0]").value("Pet Care"));
    }

    @Test
    void updateProfile_WhenBothNamesProvided_ReturnsUpdatedProfile() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(1);
        UpdateProfileResponse res = new UpdateProfileResponse("Profile updated", "Jane N.", null);
        when(userProfileService.updateProfile(eq(1), any(UpdateProfileRequest.class))).thenReturn(res);

        mockMvc.perform(patch("/api/users/me/profile")
                .header("Authorization", BEARER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson("Jane", "Nkosi", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Jane N."));

        verify(userProfileService).updateProfile(eq(1), any(UpdateProfileRequest.class));
    }

    @Test
    void updateProfile_WhenUserNotFound_ReturnsNotFound() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(99);
        when(userProfileService.updateProfile(eq(99), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        mockMvc.perform(patch("/api/users/me/profile")
                .header("Authorization", BEARER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson("Jane", null, null)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfile_WhenSkillsInvalid_ReturnsBadRequest() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(1);
        when(userProfileService.updateProfile(eq(1), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more requested skills are invalid"));

        mockMvc.perform(patch("/api/users/me/profile")
                .header("Authorization", BEARER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson(null, null, List.of("NotARealSkill"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfile_WhenUserIsNotHelperButSendsSkills_ReturnsBadRequest() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(1);
        when(userProfileService.updateProfile(eq(1), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not registered as helper"));

        mockMvc.perform(patch("/api/users/me/profile")
                .header("Authorization", BEARER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson(null, null, List.of("Pet Care"))))
                .andExpect(status().isBadRequest());
    }
}