package com.app.api.unit.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.app.api.models.Analytics;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.services.AnalyticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.api.controllers.AnalyticsController;

@WebMvcTest(
    controllers = AnalyticsController.class, 
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, 
        classes = FirebaseAuthenticationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class AnalyticsControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnalyticsService analyticsService;

    private Analytics sample;

    @BeforeEach
    void setUp(){
        sample = Analytics.builder().analyticsid(1).build();
    }

    @Test
    void getAllAnalytics_returnsList() throws Exception{
        when(analyticsService.getAllAnalytics()).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/analytics"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].analyticsid").value(1));

        verify(analyticsService, times(1)).getAllAnalytics();
    }

    @Test
    void getAllAnalytics_emptyList_returnsEmptyArray() throws Exception{
        when(analyticsService.getAllAnalytics()).thenReturn(List.of());

        mockMvc.perform(get("/api/analytics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAnalyticsById_found_returnRecord() throws Exception{
        when(analyticsService.getAnalyticsById(1)).thenReturn(sample);

        mockMvc.perform(get("/api/analytics/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.analyticsid").value(1));
    }

    @Test
    void getAnalyticsById_notFound_returns404() throws Exception{
        when(analyticsService.getAnalyticsById(99)).thenReturn(null);

        mockMvc.perform(get("/api/analytics/99"))
        .andExpect(status().isNotFound());
    }

    @Test
    void createAnalytics_returns201AndBody() throws Exception{
        when(analyticsService.saveAnalytics(any(Analytics.class))).thenReturn(sample);

        mockMvc.perform(post("/api/analytics")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sample)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.analyticsid").value(1));

        verify(analyticsService).saveAnalytics(any(Analytics.class));
    }

    @Test
    void updateAnalytics_found_returnsUpdated() throws Exception{
        Analytics updated = Analytics.builder().analyticsid(1).build();

        when(analyticsService.getAnalyticsById(1)).thenReturn(sample);
        when(analyticsService.updateAnalytics(anyInt(), any(Analytics.class))).thenReturn(updated);

        mockMvc.perform(put("/api/analytics/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.analyticsid").value(1));

        verify(analyticsService).updateAnalytics(anyInt(), any(Analytics.class));
    }

    @Test
    void updateAnalytics_notFound_returns404() throws Exception{
        when(analyticsService.getAnalyticsById(99)).thenReturn(null);

        mockMvc.perform(put("/api/analytics/99")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sample)))
        .andExpect(status().isNotFound());

        verify(analyticsService, times(0)).updateAnalytics(anyInt(), any(Analytics.class));
    }

    @Test
    void deleteAnalytics_found_retuns204() throws Exception{
        when(analyticsService.getAnalyticsById(1)).thenReturn(sample);

        mockMvc.perform(delete("/api/analytics/1")).andExpect(status().isNoContent());

        verify(analyticsService).deleteAnalytics(1);
    }

    @Test
    void deleteAnalytics_notFound_returns404() throws Exception {
        when(analyticsService.getAnalyticsById(99)).thenReturn(null);

        mockMvc.perform(delete("/api/analytics/99"))
                .andExpect(status().isNotFound());

        verify(analyticsService, times(0)).deleteAnalytics(anyInt());
    }
}
