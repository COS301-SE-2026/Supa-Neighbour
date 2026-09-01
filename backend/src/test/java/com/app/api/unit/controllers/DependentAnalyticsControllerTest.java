package com.app.api.unit.controllers;

import com.app.api.controllers.DependentAnalyticsController;
import com.app.api.models.DependentAnalytics;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.services.DependentAnalyticsService;
import com.app.api.services.FirebaseAuthService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DependentAnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class DependentAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DependentAnalyticsService dependentAnalyticsService;

    // Required because FirebaseAuthenticationFilter is loaded
    @MockBean
    private FirebaseAuthService firebaseAuthService;

    @MockBean
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    @Test
    void getAllDependentAnalytics_shouldReturn200() throws Exception {

        DependentAnalytics analytics1 = new DependentAnalytics();
        analytics1.setDependentanalyticsid("1");

        DependentAnalytics analytics2 = new DependentAnalytics();
        analytics2.setDependentanalyticsid("2");

        when(dependentAnalyticsService.getAllDependentAnalytics())
                .thenReturn(Arrays.asList(analytics1, analytics2));

        mockMvc.perform(get("/api/dependent-analytics"))
                .andExpect(status().isOk());
    }


    @Test
    void getAllDependentAnalytics_shouldReturnEmptyList() throws Exception {

        when(dependentAnalyticsService.getAllDependentAnalytics())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/dependent-analytics"))
                .andExpect(status().isOk());
    }

    @Test
    void getDependentAnalyticsById_shouldReturn200WhenFound() throws Exception {

        DependentAnalytics analytics = new DependentAnalytics();
        analytics.setDependentanalyticsid("1");

        when(dependentAnalyticsService.getDependentAnalyticsById("1"))
                .thenReturn(analytics);

        mockMvc.perform(get("/api/dependent-analytics/1"))
                .andExpect(status().isOk());
    }


    @Test
    void getDependentAnalyticsById_shouldReturn404WhenNotFound() throws Exception {

        when(dependentAnalyticsService.getDependentAnalyticsById("1"))
                .thenReturn(null);

        mockMvc.perform(get("/api/dependent-analytics/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createDependentAnalytics_shouldReturn201() throws Exception {

        DependentAnalytics analytics = new DependentAnalytics();
        analytics.setDependentanalyticsid("1");
        analytics.setTotaltasks(10);

        when(dependentAnalyticsService.saveDependentAnalytics(any(DependentAnalytics.class)))
                .thenReturn(analytics);

        String json = """
                {
                    "dependentanalyticsid": "1",
                    "totaltasks": 10,
                    "aveeragerating": 4.5,
                    "averagegivingrating": 4.0
                }
                """;

        mockMvc.perform(post("/api/dependent-analytics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void updateDependentAnalytics_shouldReturn200WhenFound() throws Exception {

        DependentAnalytics existing = new DependentAnalytics();
        existing.setDependentanalyticsid("1");

        DependentAnalytics updated = new DependentAnalytics();
        updated.setDependentanalyticsid("1");
        updated.setTotaltasks(20);

        when(dependentAnalyticsService.getDependentAnalyticsById("1"))
                .thenReturn(existing);

        when(dependentAnalyticsService.updateDependentAnalytics(
                eq("1"),
                any(DependentAnalytics.class)))
                .thenReturn(updated);

        String json = """
                {
                    "dependentanalyticsid": "1",
                    "totaltasks": 20,
                    "aveeragerating": 4.8,
                    "averagegivingrating": 4.2
                }
                """;

        mockMvc.perform(put("/api/dependent-analytics/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }


    @Test
    void updateDependentAnalytics_shouldReturn404WhenNotFound() throws Exception {

        when(dependentAnalyticsService.getDependentAnalyticsById("1"))
                .thenReturn(null);

        String json = """
                {
                    "dependentanalyticsid": "1",
                    "totaltasks": 20
                }
                """;

        mockMvc.perform(put("/api/dependent-analytics/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteDependentAnalytics_shouldReturn204WhenFound() throws Exception {

        DependentAnalytics analytics = new DependentAnalytics();
        analytics.setDependentanalyticsid("1");

        when(dependentAnalyticsService.getDependentAnalyticsById("1"))
                .thenReturn(analytics);

        doNothing().when(dependentAnalyticsService)
                .deleteDependentAnalytics("1");

        mockMvc.perform(delete("/api/dependent-analytics/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    void deleteDependentAnalytics_shouldReturn404WhenNotFound() throws Exception {

        when(dependentAnalyticsService.getDependentAnalyticsById("1"))
                .thenReturn(null);

        mockMvc.perform(delete("/api/dependent-analytics/1"))
                .andExpect(status().isNotFound());
    }
}
