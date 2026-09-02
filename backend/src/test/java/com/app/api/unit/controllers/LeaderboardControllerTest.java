package com.app.api.unit.controllers;

import com.app.api.controllers.LeaderboardController;
import com.app.api.dtos.LeaderboardEntry;
import com.app.api.dtos.LeaderboardResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.LeaderboardService;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardControllerTest {

    @Mock private LeaderboardService leaderboardService;
    @Mock private FirebaseAuthService firebaseAuthService;

    @InjectMocks
    private LeaderboardController leaderboardController;

    private MockMvc mockMvc;

    private static final String BEARER  = "Bearer valid-token";
    private static final String RAW     = "valid-token";
    private static final int    USER_ID = 1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(leaderboardController).build();
    }

    // ---------- GET /api/leaderboard ----------

    @Test
    void getLeaderboard_WhenTokenValid_ReturnsOk() throws Exception {
        LeaderboardEntry entry = new LeaderboardEntry(1, USER_ID, "John S.", 4.8, 5);
        LeaderboardResponse res = new LeaderboardResponse(
                "Hillcrest", "averageRating", List.of(entry), entry);

        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(USER_ID);
        when(leaderboardService.getLeaderboard(USER_ID, 10)).thenReturn(res);

        mockMvc.perform(get("/api/leaderboard")
                .header("Authorization", BEARER)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.neighbourhood").value("Hillcrest"))
                .andExpect(jsonPath("$.rankBy").value("averageRating"))
                .andExpect(jsonPath("$.leaderboard[0].rank").value(1))
                .andExpect(jsonPath("$.leaderboard[0].displayName").value("John S."))
                .andExpect(jsonPath("$.leaderboard[0].level").value("Gold"))
                .andExpect(jsonPath("$.currentUser.userId").value(USER_ID));

        verify(leaderboardService).getLeaderboard(USER_ID, 10);
    }

    @Test
    void getLeaderboard_WhenCustomLimitProvided_PassesLimitToService() throws Exception {
        LeaderboardResponse res = new LeaderboardResponse(
                "Hillcrest", "averageRating", List.of(), null);

        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(USER_ID);
        when(leaderboardService.getLeaderboard(USER_ID, 5)).thenReturn(res);

        mockMvc.perform(get("/api/leaderboard")
                .param("limit", "5")
                .header("Authorization", BEARER)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(leaderboardService).getLeaderboard(USER_ID, 5);
    }

    @Test
    void getLeaderboard_WhenRankByIsXp_ReturnsOk() throws Exception {
        LeaderboardResponse res = new LeaderboardResponse(
                "Hillcrest", "averageRating", List.of(), null);

        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(USER_ID);
        when(leaderboardService.getLeaderboard(USER_ID, 10)).thenReturn(res);

        mockMvc.perform(get("/api/leaderboard")
                .param("rankBy", "xp")
                .header("Authorization", BEARER)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(leaderboardService).getLeaderboard(USER_ID, 10);
    }

    @Test
    void getLeaderboard_WhenRankByIsInvalid_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/leaderboard")
                .param("rankBy", "invalid")
                .header("Authorization", BEARER))
                .andExpect(status().isBadRequest());

        verify(firebaseAuthService, never()).getUserIdFromToken(anyString());
        verify(leaderboardService, never()).getLeaderboard(anyInt(), anyInt());
    }

    @Test
    void getLeaderboard_WhenTokenInvalid_ReturnsUnauthorized() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(RAW))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(get("/api/leaderboard")
                .header("Authorization", BEARER))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid or expired Firebase token"));

        verify(leaderboardService, never()).getLeaderboard(anyInt(), anyInt());
    }

    @Test
    void getLeaderboard_WhenLeaderboardIsEmpty_ReturnsEmptyList() throws Exception {
        LeaderboardResponse res = new LeaderboardResponse(
                "Hillcrest", "averageRating", List.of(), null);

        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(USER_ID);
        when(leaderboardService.getLeaderboard(USER_ID, 10)).thenReturn(res);

        mockMvc.perform(get("/api/leaderboard")
                .header("Authorization", BEARER)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leaderboard.length()").value(0));
    }

    @Test
    void getLeaderboard_WhenCurrentUserNotOnLeaderboard_CurrentUserIsNull() throws Exception {
        LeaderboardEntry other = new LeaderboardEntry(1, 99, "Jane D.", 4.5, 7);
        LeaderboardResponse res = new LeaderboardResponse(
                "Hillcrest", "averageRating", List.of(other), null);

        when(firebaseAuthService.getUserIdFromToken(RAW)).thenReturn(USER_ID);
        when(leaderboardService.getLeaderboard(USER_ID, 10)).thenReturn(res);

        mockMvc.perform(get("/api/leaderboard")
                .header("Authorization", BEARER)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentUser").value(org.hamcrest.Matchers.nullValue()));
    }
}
