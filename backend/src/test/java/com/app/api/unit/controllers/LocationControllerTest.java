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
import com.app.api.controllers.LocationController;
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

import com.app.api.models.Location;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.services.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
    controllers = LocationController.class, 
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, 
        classes = FirebaseAuthenticationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class LocationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LocationService locationService;

    private Location sample;

    @BeforeEach
    void setUp(){
        sample = Location.builder().locationid(1).locationCenterPoint(100).locationRadius(500)
        .neighbourhoodid(10).neighbourhoodName("Willow Park").build();
    }

    @Test
    void getAllLocations_returnsList() throws Exception{
        when(locationService.getAllLocations()).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/locations"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].locationid").value(1))
        .andExpect(jsonPath("$[0].neighbourhoodName").value("Willow Park"));

        verify(locationService, times(1)).getAllLocations();
    }

    @Test
    void getAllLocations_emptyList_returnsEmptyArray() throws Exception{
        when(locationService.getAllLocations()).thenReturn(List.of());

        mockMvc.perform(get("/api/locations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getLocationById_found_returnsRecord() throws Exception{
        when(locationService.getLocationById(1)).thenReturn(sample);

        mockMvc.perform(get("/api/locations/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.locationid").value(1));
    }

    @Test
    void getLocationById_notFound_return404() throws Exception{
        when(locationService.getLocationById(99)).thenReturn(null);

        mockMvc.perform(get("/api/locations/99"))
        .andExpect(status().isNotFound());
    }

    @Test
    void createLocation_returns201AndBody() throws Exception{
        when(locationService.saveLocation(any(Location.class))).thenReturn(sample);

        mockMvc.perform(post("/api/locations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sample)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.locationid").value(1));

        verify(locationService).saveLocation(any(Location.class));
    }

    @Test
    void updateLocation_found_returnsUpdated() throws Exception{
        Location updated = Location.builder()
        .locationid(1)
        .locationRadius(750)
        .locationCenterPoint(200)
        .neighbourhoodid(10)
        .neighbourhoodName("Willow Park East")
        .build();

        when(locationService.getLocationById(1)).thenReturn(sample);
        when(locationService.updateLocation(anyInt(), any(Location.class))).thenReturn(updated);
        
        mockMvc.perform(put("/api/locations/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.neighbourhoodName").value("Willow Park East"))
        .andExpect(jsonPath("$.locationRadius").value(750));

        verify(locationService).updateLocation(anyInt(), any(Location.class));
    }

    @Test
    void updateLocation_notFound_returns404() throws Exception{
        when(locationService.getLocationById(99)).thenReturn(null);

        mockMvc.perform(put("/api/locations/99")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sample)))
        .andExpect(status().isNotFound());


        verify(locationService, times(0)).updateLocation(anyInt(), any(Location.class));
    }
    @Test
    void deleteLocation_found_returns204() throws Exception{
        when(locationService.getLocationById(1)).thenReturn(sample);

        mockMvc.perform(delete("/api/locations/1"))
        .andExpect(status().isNoContent());

        verify(locationService).deleteLocation(1);
    }

    @Test
    void deleteLocation_notFound_returns404() throws Exception {
        when(locationService.getLocationById(99)).thenReturn(null);

        mockMvc.perform(delete("/api/locations/99"))
                .andExpect(status().isNotFound());

        verify(locationService, times(0)).deleteLocation(anyInt());
    }

}
