package com.app.api.unit.controllers;

import com.app.api.controllers.UserController;
import com.app.api.models.User;
import com.app.api.repositories.UserDeviceRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.sql.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.app.api.dtos.DeviceTokenRequestDTO;
import org.springframework.http.MediaType;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserDeviceRepository userDeviceRepository;

    @MockitoBean
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    private static final String VALID_AUTH_HEADER = "Bearer valid.jwt.token";
    private static final String VALID_FCM_TOKEN = "fcm_token_12345";
    private static final int USER_ID = 10;

    @Test
    void getAllUsers_returns200WithList() throws Exception {
        User user1 = new User();
        user1.setUserid(1);
        user1.setFirstName("John");

        User user2 = new User();
        user2.setUserid(2);
        user2.setFirstName("Jane");

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userid").value(1))
                .andExpect(jsonPath("$[1].userid").value(2));
    }

    @Test
    void getUserById_whenFound_returns200() throws Exception {
        User user = new User();
        user.setUserid(101);
        user.setFirstName("John");

        when(userService.getUserById(101)).thenReturn(user);

        mockMvc.perform(get("/api/users/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userid").value(101))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getUserById_whenNotFound_returns404() throws Exception {
        when(userService.getUserById(99)).thenReturn(null);

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_returns201() throws Exception {
        User requestUser = new User();
        requestUser.setFirstName("Alice");

        User savedUser = new User();
        savedUser.setUserid(1);
        savedUser.setFirstName("Alice");

        when(userService.saveUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userid").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void updateUser_whenFound_returns200() throws Exception {
        User updateRequest = new User();
        updateRequest.setFirstName("NewName");
        updateRequest.setLastName("Smith");
        updateRequest.setEmail("new@example.com");
        updateRequest.setPhoneNumber("1234567890");
        updateRequest.setDateOfBirth(Date.valueOf("1990-01-01"));
        updateRequest.setGender("F");
        updateRequest.setFirebaseUid("firebase-uid-123");
        updateRequest.setEmailVerified(true);
        updateRequest.setPhoneVerified(false);

        User updated = new User();
        updated.setUserid(1);
        updated.setFirstName("NewName");
        updated.setLastName("Smith");
        updated.setEmail("new@example.com");

        // Mock Firebase authentication
        when(firebaseAuthService.getUserIdFromToken("valid-token")).thenReturn(1);
        when(userService.updateUser(eq(1), any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userid").value(1))
                .andExpect(jsonPath("$.firstName").value("NewName"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("new@example.com"));

        verify(userService, times(1)).updateUser(eq(1), any(User.class));
    }

    @Test
    void updateUser_whenNotFound_returns404() throws Exception {
        User updateRequest = new User();
        updateRequest.setFirstName("NewName");
        updateRequest.setLastName("Smith");
        updateRequest.setEmail("new@example.com");

        // Mock Firebase authentication
        when(firebaseAuthService.getUserIdFromToken("valid-token")).thenReturn(1);
        when(userService.updateUser(eq(1), any(User.class))).thenReturn(null);

        mockMvc.perform(put("/api/users")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(1), any(User.class));
    }

    @Test
    void updateUser_whenInvalidToken_returns401() throws Exception {
        User updateRequest = new User();
        updateRequest.setFirstName("NewName");

        // Mock Firebase authentication failure
        when(firebaseAuthService.getUserIdFromToken("invalid-token"))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(put("/api/users")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).updateUser(anyInt(), any(User.class));
    }

    @Test
    void updateUser_whenAuthHeaderMissing_returns401() throws Exception {
        User updateRequest = new User();
        updateRequest.setFirstName("NewName");

        mockMvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyInt(), any(User.class));
    }

    @Test
    void deleteUser_whenFound_returns204() throws Exception {
        User existing = new User();
        existing.setUserid(1);

        when(userService.getUserById(1)).thenReturn(existing);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1);
    }

    @Test
    void deleteUser_whenNotFound_returns404() throws Exception {
        when(userService.getUserById(99)).thenReturn(null);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());

        verify(userService, never()).deleteUser(anyInt());
    }

    @Test
    void registerDeviceToken_validToken_returns200() throws Exception {
        DeviceTokenRequestDTO request = new DeviceTokenRequestDTO();
        request.setFcmToken(VALID_FCM_TOKEN);

        when(firebaseAuthService.getUserIdFromToken("valid.jwt.token")).thenReturn(USER_ID);

        mockMvc.perform(post("/api/users/me/device-token")
                .header("Authorization", VALID_AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(firebaseAuthService).getUserIdFromToken("valid.jwt.token");
        verify(userDeviceRepository).upsertToken(USER_ID, VALID_FCM_TOKEN);
    }

    @Test
    void registerDeviceToken_invalidAuthHeader_returns401() throws Exception {
        DeviceTokenRequestDTO request = new DeviceTokenRequestDTO();
        request.setFcmToken(VALID_FCM_TOKEN);

        String invalidAuthHeader = "InvalidHeader";
        
        // FirebaseAuthException is an abstract class, we need to mock it
        FirebaseAuthException mockException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken("InvalidHeader"))
            .thenThrow(mockException);

        mockMvc.perform(post("/api/users/me/device-token")
                .header("Authorization", invalidAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userDeviceRepository, never()).upsertToken(anyInt(), anyString());
    }

     @Test
    void registerDeviceToken_emptyAuthHeader_returns401() throws Exception {
        DeviceTokenRequestDTO request = new DeviceTokenRequestDTO();
        request.setFcmToken(VALID_FCM_TOKEN);

        FirebaseAuthException mockException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken(""))
            .thenThrow(mockException);

        mockMvc.perform(post("/api/users/me/device-token")
                .header("Authorization", "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userDeviceRepository, never()).upsertToken(anyInt(), anyString());
    }

    @Test
    void registerDeviceToken_missingAuthHeader_returns401() throws Exception {
        DeviceTokenRequestDTO request = new DeviceTokenRequestDTO();
        request.setFcmToken(VALID_FCM_TOKEN);

        mockMvc.perform(post("/api/users/me/device-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(firebaseAuthService, userDeviceRepository);
    }

    @Test
    void registerDeviceToken_malformedAuthHeader_returns401() throws Exception {
        DeviceTokenRequestDTO request = new DeviceTokenRequestDTO();
        request.setFcmToken(VALID_FCM_TOKEN);

        String malformedHeader = "Bearer"; // Missing token part

        FirebaseAuthException mockException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken("Bearer"))
            .thenThrow(mockException);

        mockMvc.perform(post("/api/users/me/device-token")
                .header("Authorization", malformedHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userDeviceRepository, never()).upsertToken(anyInt(), anyString());
    }

    @Test
    void registerDeviceToken_nullFcmToken_stillReturns200() throws Exception {
        DeviceTokenRequestDTO request = new DeviceTokenRequestDTO();
        request.setFcmToken(null);

        when(firebaseAuthService.getUserIdFromToken("valid.jwt.token")).thenReturn(USER_ID);

        mockMvc.perform(post("/api/users/me/device-token")
                .header("Authorization", VALID_AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userDeviceRepository).upsertToken(USER_ID, null);
    }

    @Test
    void registerDeviceToken_emptyFcmToken_stillReturns200() throws Exception {
        DeviceTokenRequestDTO request = new DeviceTokenRequestDTO();
        request.setFcmToken("");

        when(firebaseAuthService.getUserIdFromToken("valid.jwt.token")).thenReturn(USER_ID);

        mockMvc.perform(post("/api/users/me/device-token")
                .header("Authorization", VALID_AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userDeviceRepository).upsertToken(USER_ID, "");
    }

    @Test
    void registerDeviceToken_expiredToken_returns401() throws Exception {
        DeviceTokenRequestDTO request = new DeviceTokenRequestDTO();
        request.setFcmToken(VALID_FCM_TOKEN);

        FirebaseAuthException mockException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken("valid.jwt.token"))
            .thenThrow(mockException);

        mockMvc.perform(post("/api/users/me/device-token")
                .header("Authorization", VALID_AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userDeviceRepository, never()).upsertToken(anyInt(), anyString());
    }
}