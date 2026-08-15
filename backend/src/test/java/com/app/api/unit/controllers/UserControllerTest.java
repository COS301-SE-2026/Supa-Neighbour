package com.app.api.unit.controllers;

import com.app.api.controllers.UserController;
import com.app.api.models.User;
import com.app.api.repositories.UserDeviceRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        User existing = new User();
        existing.setUserid(1);
        existing.setFirstName("OldName");

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

        when(userService.getUserById(1)).thenReturn(existing);
        when(userService.updateUser(eq(1), any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userid").value(1))
                .andExpect(jsonPath("$.firstName").value("NewName"));
    }

    @Test
    void updateUser_whenNotFound_returns404() throws Exception {
        User updateRequest = new User();
        updateRequest.setFirstName("NewName");

        when(userService.getUserById(99)).thenReturn(null);

        mockMvc.perform(put("/api/users/99")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

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

    
}