package com.app.api.unit.controllers;

import com.app.api.controllers.CompatibilityController;
import com.app.api.models.Compatibility;
import com.app.api.models.Dependent;
import com.app.api.models.Helper;
import com.app.api.services.CompatibilityService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CompatibilityControllerTest {

    @Mock
    private CompatibilityService compatibilityService;

    @InjectMocks
    private CompatibilityController compatibilityController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Compatibility compatibility;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(compatibilityController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();

        Helper helper = new Helper();
        helper.setHelperid(1);

        Dependent dependent = new Dependent();
        dependent.setDependentId(3);

        compatibility = new Compatibility();
        compatibility.setCompatibilityid(1);
        compatibility.setHelperid(helper);
        compatibility.setDependentid(dependent);
        compatibility.setCompatibilityScore(85);
        compatibility.setCompatibilityColour("Green");
    }

    @Test
    void getAllCompatibility_ListOfCompatibility() throws Exception {

        List<Compatibility> compatibilities = Arrays.asList(compatibility);
        when(compatibilityService.getAllCompatibility()).thenReturn(compatibilities);

        mockMvc.perform(get("/api/compatibility")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].compatibilityid").value(1))
                .andExpect(jsonPath("$[0].compatibilityScore").value(85));

        verify(compatibilityService, times(1)).getAllCompatibility();
    }

    @Test
    void getAllCompatibility_WhenEmpty_EmptyList() throws Exception {

        when(compatibilityService.getAllCompatibility()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/compatibility")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(compatibilityService, times(1)).getAllCompatibility();
    }

    @Test
    void getCompatibilityById_ReturnCompatibility() throws Exception {

        when(compatibilityService.getCompatibilityById(1)).thenReturn(compatibility);

        mockMvc.perform(get("/api/compatibility/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compatibilityid").value(1))
                .andExpect(jsonPath("$.compatibilityColour").value("Green"));

        verify(compatibilityService, times(1)).getCompatibilityById(1);
    }

    @Test
    void getCompatibilityById_WhenNotExisting_NotFound() throws Exception {

        when(compatibilityService.getCompatibilityById(999)).thenReturn(null);

        mockMvc.perform(get("/api/compatibility/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(compatibilityService, times(1)).getCompatibilityById(999);
    }

    @Test
    void createCompatibility_CreatedCompatibility() throws Exception {

        when(compatibilityService.saveCompatibility(any(Compatibility.class))).thenReturn(compatibility);

        mockMvc.perform(post("/api/compatibility")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compatibility)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.compatibilityid").value(1))
                .andExpect(jsonPath("$.compatibilityScore").value(85));

        verify(compatibilityService, times(1)).saveCompatibility(any(Compatibility.class));
    }

    @Test
    void updateCompatibility_WhenExists() throws Exception {

        Compatibility updatedCompatibility = new Compatibility();
        updatedCompatibility.setCompatibilityid(1);
        updatedCompatibility.setCompatibilityScore(95);
        updatedCompatibility.setCompatibilityColour("Green");

        when(compatibilityService.getCompatibilityById(1)).thenReturn(compatibility);
        when(compatibilityService.updateCompatibility(eq(1), any(Compatibility.class))).thenReturn(updatedCompatibility);

        mockMvc.perform(put("/api/compatibility/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCompatibility)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compatibilityScore").value(95));

        verify(compatibilityService, times(1)).getCompatibilityById(1);
        verify(compatibilityService, times(1)).updateCompatibility(eq(1), any(Compatibility.class));
    }

    @Test
    void updateCompatibility_WhenNotExists() throws Exception {

        when(compatibilityService.getCompatibilityById(999)).thenReturn(null);

        mockMvc.perform(put("/api/compatibility/999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compatibility)))
                .andExpect(status().isNotFound());

        verify(compatibilityService, times(1)).getCompatibilityById(999);
        verify(compatibilityService, never()).updateCompatibility(anyInt(), any(Compatibility.class));
    }

    @Test
    void deleteCompatibility_WhenExisting() throws Exception {

        when(compatibilityService.getCompatibilityById(1)).thenReturn(compatibility);
        doNothing().when(compatibilityService).deleteCompatibility(1);

        mockMvc.perform(delete("/api/compatibility/1"))
                .andExpect(status().isNoContent());

        verify(compatibilityService, times(1)).getCompatibilityById(1);
        verify(compatibilityService, times(1)).deleteCompatibility(1);
    }

    @Test
    void deleteCompatibility_WhenNotExisting() throws Exception {

        when(compatibilityService.getCompatibilityById(999)).thenReturn(null);

        mockMvc.perform(delete("/api/compatibility/999"))
                .andExpect(status().isNotFound());

        verify(compatibilityService, times(1)).getCompatibilityById(999);
        verify(compatibilityService, never()).deleteCompatibility(anyInt());
    }
}