package com.app.api.unit.controllers;

import com.app.api.controllers.AvailabilityController;
import com.app.api.models.Availability;
import com.app.api.repositories.UserRepository;
import com.app.api.services.AvailabilityService;
import com.app.api.services.FirebaseAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailabilityController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AvailabilityService availabilityService;

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    public void testGetAllAvailability_returns200() throws Exception {
        Availability a1 = new Availability();
        a1.setAvailabilityid(1);
        a1.setDayofweek("MONDAY");

        Availability a2 = new Availability();
        a2.setAvailabilityid(2);
        a2.setDayofweek("TUESDAY");

        when(availabilityService.getAllAvailability()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].availabilityid").value(1))
                .andExpect(jsonPath("$[1].availabilityid").value(2));
    }

    @Test
    void getAvailabilityById_whenFound_returns200() throws Exception {
        Availability availability = new Availability();
        availability.setAvailabilityid(1);
        availability.setDayofweek("MONDAY");
        availability.setTimewindow("09:00");
        availability.setIsactive(true);

        when(availabilityService.getAvailabilityById(1)).thenReturn(availability);

        mockMvc.perform(get("/api/availability/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availabilityid").value(1))
                .andExpect(jsonPath("$.dayofweek").value("MONDAY"))
                .andExpect(jsonPath("$.timewindow").value("09:00"))
                .andExpect(jsonPath("$.isactive").value(true));
    }

    @Test
    void getAvailabilityById_whenNotFound_returns404() throws Exception {
        when(availabilityService.getAvailabilityById(anyInt())).thenReturn(null);

        mockMvc.perform(get("/api/availability/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAvailability_returns201() throws Exception {
        Availability request = new Availability();
        request.setDayofweek("WEDNESDAY");
        request.setTimewindow("10:00");
        request.setIsactive(true);

        Availability saved = new Availability();
        saved.setAvailabilityid(1);
        saved.setDayofweek("WEDNESDAY");
        saved.setTimewindow("10:00");
        saved.setIsactive(true);

        when(availabilityService.saveAvailability(any(Availability.class))).thenReturn(saved);

        mockMvc.perform(post("/api/availability")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.availabilityid").value(1))
                .andExpect(jsonPath("$.dayofweek").value("WEDNESDAY"));
    }

    @Test
    void updateAvailability_whenFound_returns200() throws Exception {
        Availability request = new Availability();
        request.setDayofweek("THURSDAY");
        request.setTimewindow("11:00");
        request.setIsactive(true);

        Availability updated = new Availability();
        updated.setAvailabilityid(1);
        updated.setDayofweek("THURSDAY");
        updated.setTimewindow("11:00");
        updated.setIsactive(true);

        when(availabilityService.updateAvailability(eq(1), any(Availability.class))).thenReturn(updated);

        mockMvc.perform(put("/api/availability/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availabilityid").value(1))
                .andExpect(jsonPath("$.dayofweek").value("THURSDAY"))
                .andExpect(jsonPath("$.timewindow").value("11:00"))
                .andExpect(jsonPath("$.isactive").value(true));
    }

    @Test
    void updateAvailability_whenNotFound_returns404() throws Exception {
        Availability request = new Availability();
        request.setDayofweek("TUESDAY");

        when(availabilityService.updateAvailability(eq(99), any(Availability.class))).thenReturn(null);

        mockMvc.perform(put("/api/availability/99")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAvailability_whenFound_returns204() throws Exception {
        when(availabilityService.deleteAvailability(1)).thenReturn(true);

        mockMvc.perform(delete("/api/availability/1"))
                .andExpect(status().isNoContent());

        verify(availabilityService).deleteAvailability(1);
    }

    @Test
    void deleteAvailability_whenNotFound_returns404() throws Exception {
        when(availabilityService.deleteAvailability(99)).thenReturn(false);

        mockMvc.perform(delete("/api/availability/99"))
                .andExpect(status().isNotFound());
    }
}
