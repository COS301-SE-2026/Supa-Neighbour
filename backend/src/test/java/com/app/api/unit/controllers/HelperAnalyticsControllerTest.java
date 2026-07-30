package com.app.api.unit.controllers;

import com.app.api.controllers.HelperAnalyticsController;
import com.app.api.models.Compatibility;
import com.app.api.models.HelperAnalytics;
import com.app.api.models.Location;
import com.app.api.models.TaskType;
import com.app.api.models.User;
import com.app.api.services.HelperAnalyticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HelperAnalyticsControllerTest {

    @Mock
    private HelperAnalyticsService helperAnalyticsService;

    @InjectMocks
    private HelperAnalyticsController helperAnalyticsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private HelperAnalytics helperAnalytics;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(helperAnalyticsController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();

        User user = new User();

        TaskType taskType = new TaskType();
        taskType.setTasktypeid(1);

        Location location = new Location();
        location.setLocationid(1);

        Compatibility compatibility = new Compatibility();
        compatibility.setCompatibilityid(1);

        helperAnalytics = new HelperAnalytics();
        helperAnalytics.setHelperAnalyticsid("HA1");
        helperAnalytics.setUserid(user);
        helperAnalytics.setTasktypeid(taskType);
        helperAnalytics.setLocationid(location);
        helperAnalytics.setCompatibilityid(compatibility);
        helperAnalytics.setAverageRating(4.5f);
        helperAnalytics.setAverageGivingRating(4.0f);
    }

    @Test
    void getAllHelperAnalytics_ListOfHelperAnalytics() throws Exception {

        List<HelperAnalytics> analyticsList = Arrays.asList(helperAnalytics);
        when(helperAnalyticsService.getAllHelperAnalytics()).thenReturn(analyticsList);

        mockMvc.perform(get("/api/helper-analytics")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].helperAnalyticsid").value("HA1"))
                .andExpect(jsonPath("$[0].averageRating").value(4.5));

        verify(helperAnalyticsService, times(1)).getAllHelperAnalytics();
    }

    @Test
    void getAllHelperAnalytics_WhenEmpty_EmptyList() throws Exception {

        when(helperAnalyticsService.getAllHelperAnalytics()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/helper-analytics")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(helperAnalyticsService, times(1)).getAllHelperAnalytics();
    }

    @Test
    void getHelperAnalyticsById_ReturnHelperAnalytics() throws Exception {

        when(helperAnalyticsService.getHelperAnalyticsById("HA1")).thenReturn(helperAnalytics);

        mockMvc.perform(get("/api/helper-analytics/HA1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.helperAnalyticsid").value("HA1"))
                .andExpect(jsonPath("$.averageGivingRating").value(4.0));

        verify(helperAnalyticsService, times(1)).getHelperAnalyticsById("HA1");
    }

    @Test
    void getHelperAnalyticsById_WhenNotExisting_NotFound() throws Exception {

        when(helperAnalyticsService.getHelperAnalyticsById("UNKNOWN")).thenReturn(null);

        mockMvc.perform(get("/api/helper-analytics/UNKNOWN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(helperAnalyticsService, times(1)).getHelperAnalyticsById("UNKNOWN");
    }

    @Test
    void createHelperAnalytics_CreatedHelperAnalytics() throws Exception {

        when(helperAnalyticsService.saveHelperAnalytics(any(HelperAnalytics.class))).thenReturn(helperAnalytics);

        mockMvc.perform(post("/api/helper-analytics")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(helperAnalytics)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.helperAnalyticsid").value("HA1"))
                .andExpect(jsonPath("$.averageRating").value(4.5));

        verify(helperAnalyticsService, times(1)).saveHelperAnalytics(any(HelperAnalytics.class));
    }

    @Test
    void updateHelperAnalytics_WhenExists() throws Exception {

        HelperAnalytics updatedAnalytics = new HelperAnalytics();
        updatedAnalytics.setHelperAnalyticsid("HA1");
        updatedAnalytics.setAverageRating(4.9f);
        updatedAnalytics.setAverageGivingRating(4.8f);

        when(helperAnalyticsService.getHelperAnalyticsById("HA1")).thenReturn(helperAnalytics);
        when(helperAnalyticsService.updateHelperAnalytics(eq("HA1"), any(HelperAnalytics.class))).thenReturn(updatedAnalytics);

        mockMvc.perform(put("/api/helper-analytics/HA1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAnalytics)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.9))
                .andExpect(jsonPath("$.averageGivingRating").value(4.8));

        verify(helperAnalyticsService, times(1)).getHelperAnalyticsById("HA1");
        verify(helperAnalyticsService, times(1)).updateHelperAnalytics(eq("HA1"), any(HelperAnalytics.class));
    }

    @Test
    void updateHelperAnalytics_WhenNotExists() throws Exception {

        when(helperAnalyticsService.getHelperAnalyticsById("UNKNOWN")).thenReturn(null);

        mockMvc.perform(put("/api/helper-analytics/UNKNOWN")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(helperAnalytics)))
                .andExpect(status().isNotFound());

        verify(helperAnalyticsService, times(1)).getHelperAnalyticsById("UNKNOWN");
        verify(helperAnalyticsService, never()).updateHelperAnalytics(anyString(), any(HelperAnalytics.class));
    }

    @Test
    void deleteHelperAnalytics_WhenExisting() throws Exception {

        when(helperAnalyticsService.getHelperAnalyticsById("HA1")).thenReturn(helperAnalytics);
        doNothing().when(helperAnalyticsService).deleteHelperAnalytics("HA1");

        mockMvc.perform(delete("/api/helper-analytics/HA1"))
                .andExpect(status().isNoContent());

        verify(helperAnalyticsService, times(1)).getHelperAnalyticsById("HA1");
        verify(helperAnalyticsService, times(1)).deleteHelperAnalytics("HA1");
    }

    @Test
    void deleteHelperAnalytics_WhenNotExisting() throws Exception {

        when(helperAnalyticsService.getHelperAnalyticsById("UNKNOWN")).thenReturn(null);

        mockMvc.perform(delete("/api/helper-analytics/UNKNOWN"))
                .andExpect(status().isNotFound());

        verify(helperAnalyticsService, times(1)).getHelperAnalyticsById("UNKNOWN");
        verify(helperAnalyticsService, never()).deleteHelperAnalytics(anyString());
    }
}