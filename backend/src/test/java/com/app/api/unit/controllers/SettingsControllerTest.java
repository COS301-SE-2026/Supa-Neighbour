package com.app.api.unit.controllers;

import com.app.api.dtos.ModeResponse;
import com.app.api.dtos.ShowStatusRequest;
import com.app.api.dtos.ShowStatusResponse;
import com.app.api.dtos.UserStatusResponse;
import com.app.api.repositories.SettingsRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.SettingsServices;
import com.app.api.controllers.SettingsController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import com.app.api.dtos.UpdateSettingsDTO;
import com.app.api.dtos.UserSettingsDTO;
import com.app.api.models.User;
import static org.mockito.ArgumentMatchers.any;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(SettingsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SettingsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SettingsServices settingsServices;

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;


    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private SettingsRepository settingsRepository;

    private static final String VALID_TOKEN = "Bearer valid-token";

    @Test
    void getStatus_withValidToken_returns200() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);

        when(settingsServices.getUserStatus(1)).thenReturn(new UserStatusResponse(true, true, Instant.now()));

        mockMvc.perform(get("/api/settings/users/show-status")
            .header("Authorization", VALID_TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.visible").value(true))
            .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    void getStatus_withInvalidToken_returns401() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(get("/api/settings/users/show-status")
            .header("Authorization", "Bearer bad-token"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void updateShowStatus_withValidBody_returns200() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(settingsServices.updateShowStatus(eq(1), eq(true))).thenReturn(new ShowStatusResponse(true));

        ShowStatusRequest request = new ShowStatusRequest();

        request.setShowStatus(true);
        mockMvc.perform(post("/api/settings/users/show-status")
            .header("Authorization", VALID_TOKEN)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.showStatus").value(true));
    }

    @Test
    void updateShowStatus_withInvalidToken_returns401() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));

        ShowStatusRequest request = new ShowStatusRequest();
        request.setShowStatus(true);

        mockMvc.perform(post("/api/settings/users/show-status")
            .header("Authorization", "Bearer bad-token")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void updateStatus_withMissingField_returns400() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(settingsServices.updateShowStatus(eq(1), eq(null))).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid showStatus value"));

        mockMvc.perform(post("/api/settings/users/show-status")
            .header("Authorization", VALID_TOKEN)
            .contentType("application/json")
            .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getMode_withValidToken_returns200() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(settingsServices.getUserMode(1)).thenReturn(new ModeResponse("dark"));

        mockMvc.perform(get("/api/settings/users/mode")
            .header("Authorization", VALID_TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mode").value("dark"));
    }

    @Test
    void getMode_withInvalidToken_return401() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(get("/api/settings/users/mode")
            .header("Authorization", "Bearer bad-token"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void setMode_withInvalidToken_returns401() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));

        ModeResponse request = new ModeResponse("dark");

        mockMvc.perform(post("/api/settings/users/mode")
        .header("Authorization", "Bearer bad-token")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
    }

    @Test
    void setMode_withInvalidModeValue_returns400() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(settingsServices.setUserMode(eq(1), eq("sepia"))).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mode value"));

        ModeResponse request = new ModeResponse("sepia");

        mockMvc.perform(post("/api/settings/users/mode")
            .header("Authorization", VALID_TOKEN)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getStatusForUser_withValid_Token_returns200() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(settingsServices.getUserStatus(42)).thenReturn(new UserStatusResponse(true, false, Instant.now()));

        mockMvc.perform(get("/api/settings/users/42/status")
        .header("Authorization", VALID_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.visible").value(true))
        .andExpect(jsonPath("$.online").value(false));
    }

    @Test
    void getStatusForUser_withTargetVisibilityOff_returnsVisibleFalseOnly() throws  Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(settingsServices.getUserStatus(42)).thenReturn(new UserStatusResponse(false));

        mockMvc.perform(get("/api/settings/users/42/status")
        .header("Authorization", VALID_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.visible").value(false))
        .andExpect(jsonPath("$.online").doesNotExist());
    }

    @Test
    void getStatusForUser_withInvalidToken_returns401() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(get("/api/settings/users/42/status")
        .header("Authorization", "Bearer bad-token"))
        .andExpect(status().isUnauthorized());
    }

    @Test
    void setMode_withValidToken_returns200() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(settingsServices.setUserMode(eq(1), eq("dark"))).thenReturn(new ModeResponse("dark"));

        ModeResponse request = new ModeResponse("dark");

        mockMvc.perform(post("/api/settings/users/mode")
            .header("Authorization", VALID_TOKEN)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mode").value("dark"));
    }

    @Test
    void getUserInfo_withValidToken_returns200() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(settingsServices.getUserInfo(42)).thenReturn(mock(UserSettingsDTO.class));

        mockMvc.perform(get("/api/settings/users/information/42")
            .header("Authorization", VALID_TOKEN))
            .andExpect(status().isOk());
    }

    @Test
    void getUserInfo_withInvalidToken_returns401() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(get("/api/settings/users/information/42")
        .header("Authorization", "Bearer bad-token"))
        .andExpect(status().isUnauthorized());
    }

    @Test
    void updateSettings_withValidToken_returns200() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(settingsServices.updateSettings(eq(42), any())).thenReturn(mock(UserSettingsDTO.class));

        UpdateSettingsDTO dto = new UpdateSettingsDTO();

        mockMvc.perform(put("/api/settings/42")
            .header("Authorization", VALID_TOKEN)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    void updateSettings_withInvalidToken_returns401() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));

        UpdateSettingsDTO dto = new UpdateSettingsDTO();

        mockMvc.perform(put("/api/settings/42")
            .header("Authorization", "Bearer bad-token")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_withValidToken_returns204() throws Exception {
        User user = mock(User.class);
        when(user.getFirebaseUid()).thenReturn(null);
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(delete("/api/settings/me/user")
            .header("Authorization", VALID_TOKEN))
            .andExpect(status().isNoContent());

        verify(settingsRepository).deleteById(1);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_withInvalidToken_returns404() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/settings/me/user")
        .header("Authorization",VALID_TOKEN))
        .andExpect(status().isNotFound());
    }


    @Test
    void deleteUser_withInvalidToken_returns401() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(delete("/api/settings/me/user")
    .header("Authorization", "Bearer bad-token"))
    .andExpect(status().isUnauthorized());
    }
}
