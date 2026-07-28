package com.app.api.unit.controllers;

import com.app.api.controllers.AchievementsController;
import com.app.api.dtos.AchievementDTO;
import com.app.api.dtos.AchievementResponse;
import com.app.api.repositories.UserRepository;
import com.app.api.services.AchievementService;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.ErrorCode;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuthException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.springframework.context.annotation.ComponentScan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(
    value = AchievementsController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {com.app.api.security.FirebaseAuthenticationFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class AchievementControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AchievementService achievementService;


        @MockitoBean
        private FirebaseAuthService firebaseAuthService;

        @MockitoBean
        private UserRepository userRepository;

        private static final String VALID_TOKEN = "valid-token";
        private static final String AUTH_HEADER = "Bearer " + VALID_TOKEN;

        private static final int USER_ID = 42;

        @BeforeEach
        void setup(){
            reset(achievementService, firebaseAuthService);
        }

        @Test
        void getAchievements_validtToken_returnsOkWithAchievements() throws Exception{
            AchievementDTO earned = new AchievementDTO(
                1, "Helping Hand", "Completed 5 tasks", "2026-06-01"
            );

            AchievementDTO unearned = new AchievementDTO(
                    2, "Neighbourhood Hero", "Complete 25 tasks", null
            );
            unearned.setProgress("18/25");

            AchievementResponse mockResponse = new AchievementResponse(
                    List.of(earned), List.of(unearned)
            );


            when(firebaseAuthService.getUserIdFromToken(VALID_TOKEN)).thenReturn(USER_ID);
            when(achievementService.getAchievements(USER_ID)).thenReturn(mockResponse);

            
            mockMvc.perform(get("/api/users/me/achievements").header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.earned", hasSize(1)))
                .andExpect(jsonPath("$.earned[0].badgeId").value(1))
                .andExpect(jsonPath("$.earned[0].name").value("Helping Hand"))
                .andExpect(jsonPath("$.earned[0].description").value("Completed 5 tasks"))
                .andExpect(jsonPath("$.earned[0].awardedOn").value("2026-06-01"))
                .andExpect(jsonPath("$.unearned", hasSize(1)))
                .andExpect(jsonPath("$.unearned[0].badgeId").value(2))
                .andExpect(jsonPath("$.unearned[0].name").value("Neighbourhood Hero"))
                .andExpect(jsonPath("$.unearned[0].awardedOn").doesNotExist())
                .andExpect(jsonPath("$.unearned[0].progress").value("18/25"));

            verify(firebaseAuthService).getUserIdFromToken(VALID_TOKEN);
            verify(achievementService).getAchievements(USER_ID);
        }

        @Test
        void getAchievements_noAchivementsYet_returnsEmptyLists() throws Exception{
            
            when(firebaseAuthService.getUserIdFromToken(VALID_TOKEN)).thenReturn(USER_ID);
            when(achievementService.getAchievements(USER_ID)).thenReturn(new AchievementResponse(List.of(), List.of()));

            
            mockMvc.perform(get("/api/users/me/achievements").header("Authorization", AUTH_HEADER))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.earned", hasSize(0)))
            .andExpect(jsonPath("$.unearned", hasSize(0)));
        }


        @Test
        void getAchievements_invalidOrExpiredToken_returns401() throws Exception{
            doThrow(new FirebaseAuthException(ErrorCode.UNAUTHENTICATED,
            "Invalid token",
            null,
            null,
            AuthErrorCode.INVALID_ID_TOKEN
            ))
            .when(firebaseAuthService).getUserIdFromToken(anyString());

            mockMvc.perform(get("/api/users/me/achievements").header("Authorization", "Bearer bad-token")).andExpect(status().isUnauthorized()).andExpect(content().string("Invalid or expired Firebase token"));

            verify(achievementService, never()).getAchievements(anyInt());
        }

        @Test
        void getAchievements_missingAuthorizedHeader_returns400() throws Exception{
            mockMvc.perform(get("/api/users/me/achievements")).andExpect(status().isBadRequest());

            verifyNoInteractions(firebaseAuthService, achievementService);
        }
}
