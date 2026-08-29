package com.app.api.unit.controllers;

import com.app.api.dtos.HelperTaskDTO;
import com.app.api.dtos.HelperTaskResponse;
import com.app.api.security.FirebaseAuthenticationFilter; // ASSUMPTION: adjust to your actual package if different
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.HelperTasksService;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.app.api.controllers.HelperTaskController;


import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = HelperTaskController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = FirebaseAuthenticationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class HelperTasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HelperTasksService helperTaskService;

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    private HelperTaskResponse sampleResponse;

    private static final String VALID_TOKEN = "valid-token";
    private static final String AUTH_HEADER = "Bearer " + VALID_TOKEN;
    private static final int USER_ID = 42;
    private static final int HELPER_ID = 7;

    @BeforeEach
    void setUp(){
        HelperTaskDTO dto = new HelperTaskDTO(
            101, 
            "Grocery Run",
            "completed", 
            "2026-08-01",
            "2026-08-02",
            5, 
            "Great help", 
            "Surburb A", 
            3);

        sampleResponse = new HelperTaskResponse(HELPER_ID, 1, List.of(dto));
    }

    @Test
    void getMyTasks_validToken_returnsOkWithTaskHistory() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(VALID_TOKEN)).thenReturn(USER_ID);
        when(helperTaskService.getAcceptedTasks(eq(USER_ID), isNull(), eq(20), eq(0))).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/helpers/me/tasks")
            .header("Authorization", AUTH_HEADER))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.helperId").value(HELPER_ID))
        .andExpect(jsonPath("$.total").value(1))
        .andExpect(jsonPath("$.tasks[0].taskId").value(101));

        verify(helperTaskService).getAcceptedTasks(USER_ID, null, 20, 0);
    }

    @Test
    void getMyTasks_invalidToken_retirns401() throws Exception{
        when(firebaseAuthService.getUserIdFromToken("bad-token"))
            .thenThrow(new FirebaseAuthException(com.google.firebase.ErrorCode.UNAUTHENTICATED,
                "Invalid token", null, null, com.google.firebase.auth.AuthErrorCode.INVALID_ID_TOKEN));

        mockMvc.perform(get("/api/helpers/me/tasks")
        .header("Authorization", "Bearer bad-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid or expired Firebase token"));

        verifyNoInteractions(helperTaskService);
    }

    @Test
    void getMyTasks_withStatusFilterAndPagination_passesParamsThrough() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(VALID_TOKEN)).thenReturn(USER_ID);
        when(helperTaskService.getAcceptedTasks(eq(USER_ID), eq("completed"), eq(5), eq(10)))
            .thenReturn(sampleResponse);

        mockMvc.perform(get("/api/helpers/me/tasks")
        .header("Authorization", AUTH_HEADER)
        .param("statusFilter", "completed")
        .param("limit", "5")
        .param("offset", "10"))
        .andExpect(status().isOk());

        verify(helperTaskService).getAcceptedTasks(USER_ID, "completed", 5, 10);
    }

    @Test
    void getMyTasks_userNotHelper_propergates403() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(VALID_TOKEN)).thenReturn(USER_ID);
        when(helperTaskService.getAcceptedTasks(eq(USER_ID), any(), anyInt(), anyInt()))
        .thenThrow(new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.FORBIDDEN, "User is not a helper"
        ));

        mockMvc.perform(get("/api/helpers/me/tasks")
        .header("Authorization", AUTH_HEADER))
        .andExpect(status().isForbidden());
    }

    @Test
    void getMyTasks_missingAuthorizationHeader_returns400() throws Exception{
        mockMvc.perform(get("/api/helpers/me/tasks"))
        .andExpect(status().isBadRequest());

        verifyNoInteractions(firebaseAuthService, helperTaskService);
    }


}
