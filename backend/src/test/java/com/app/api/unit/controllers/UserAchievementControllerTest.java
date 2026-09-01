package com.app.api.unit.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.app.api.controllers.UserAchievementController;
import com.app.api.models.UserAchievement;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.UserAchievementService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserAchievementController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserAchievementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAchievementService userAchievementServices;

        @MockitoBean
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    @Test
    void getUserAchievement_withValidToken_returns200() throws Exception{
        when(userAchievementServices.getAllUserAchievement()).thenReturn(List.of(new UserAchievement()));

        mockMvc.perform(get("/api/userAchievement")).andExpect(status().isOk());
    }

    @Test
    void getUserAchievementById_withValidToken_returns200() throws Exception {
        UserAchievement achievement = new UserAchievement();

        when(userAchievementServices.getUserAchievementById(1)).thenReturn(achievement);

        mockMvc.perform(get("/api/userAchievement/1")).andExpect(status().isOk());
    }

    @Test
    void createUserAcheivement_withValidToken_return201() throws Exception {
        UserAchievement achievement = new UserAchievement();
        when(userAchievementServices.saveAchievement(any(UserAchievement.class))).thenReturn(achievement);

        mockMvc.perform(post("/api/userAchievement")
        .contentType("application/json").content(objectMapper.writeValueAsString(achievement)))
        .andExpect(status().isCreated());
    }

    @Test
    void updateUserAchievement_withValidToken_return200() throws Exception {
        UserAchievement existing = new UserAchievement();
        UserAchievement update = new UserAchievement();

        when(userAchievementServices.getUserAchievementById(1)).thenReturn(existing);
        when(userAchievementServices.updateUserAchievement(any(Integer.class),any(UserAchievement.class))).thenReturn(update);

        mockMvc.perform(put("/api/userAchievement/1")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(update))).andExpect(status().isOk());
    }


    @Test
    void deleteUserAchievement_withValidToken_return204() throws Exception {
        
        UserAchievement achievement = new UserAchievement();

        when(userAchievementServices.getUserAchievementById(1)).thenReturn(achievement);
        
        mockMvc.perform(delete("/api/userAchievement/1")).andExpect(status().isNoContent());

        verify(userAchievementServices).deleteUserAchievement(1);
    }
    
}
