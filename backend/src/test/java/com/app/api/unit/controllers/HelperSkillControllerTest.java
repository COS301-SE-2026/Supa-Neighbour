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

import com.app.api.models.HelperSkill;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.services.HelperSkillService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.api.controllers.HelperSkillController;

@WebMvcTest(
    controllers = HelperSkillController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = FirebaseAuthenticationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class HelperSkillControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HelperSkillService helperSkillService;

    private HelperSkill sample;

    @BeforeEach
    void setUp(){
        sample = HelperSkill.builder()
        .helperSkillId(1)
        .build();
    }

    @Test
    void getHelperSkills_returnsList() throws Exception{
        when(helperSkillService.getAllHelpersSkills()).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/helper-skills"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].helperSkillId").value(1));
    }

    @Test
    void getAllHelperSkills_emptyList_returnsEmptyArray() throws Exception{
        when(helperSkillService.getAllHelpersSkills()).thenReturn(List.of());

        mockMvc.perform(get("/api/helper-skills"))
        .andExpect(status().isOk())
        .andExpect(status().isOk());
    }

    @Test
    void getHelperSkillById_found_returnsRecord() throws Exception{
        when(helperSkillService.getHelperSkillById(1)).thenReturn(sample);

        mockMvc.perform(get("/api/helper-skills/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.helperSkillId").value(1));

        verify(helperSkillService).getHelperSkillById(1);
    }

    @Test
    void getHelperSkillById_notFound_returns404() throws Exception{
        when(helperSkillService.getHelperSkillById(99)).thenReturn(null);

        mockMvc.perform(get("/api/helper-skills/99"))
        .andExpect(status().isNotFound());
    }

    @Test
    void createHelperSkill_returns201AndBody() throws Exception{
        when(helperSkillService.saveHelperSkill(any(HelperSkill.class))).thenReturn(sample);

        mockMvc.perform(post("/api/helper-skills")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sample)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.helperSkillId").value(1));
    }

    @Test
    void createHelperSkill_serviceReturnsNull_returns400()  throws Exception{
        when(helperSkillService.saveHelperSkill(any(HelperSkill.class))).thenReturn(null);

        mockMvc.perform(post("/api/helper-skills")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sample)))
        .andExpect(status().isBadRequest());
    }

    @Test
    void updateHelperSkill_found_returnsUpdated() throws Exception{
        HelperSkill updated = HelperSkill.builder().helperSkillId(1).build();

        when(helperSkillService.getHelperSkillById(1)).thenReturn(sample);
        when(helperSkillService.updateHelperSkill(anyInt(), any(HelperSkill.class))).thenReturn(updated);

        mockMvc.perform(put("/api/helper-skills/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.helperSkillId").value(1));

        verify(helperSkillService).updateHelperSkill(anyInt(), any(HelperSkill.class));
    }

    @Test
    void updateHelperSkill_notFound_returns404() throws Exception{
        when(helperSkillService.getHelperSkillById(99)).thenReturn(null);

        mockMvc.perform(put("/api/helper-skills/99")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sample)))
        .andExpect(status().isNotFound());

        verify(helperSkillService, times(0)).updateHelperSkill(anyInt(), any(HelperSkill.class));
    }

    @Test
    void deleteHelperSkill_found_returns204() throws Exception{
        when(helperSkillService.deleteHelperSkill(1)).thenReturn(true);
        
        mockMvc.perform(delete("/api/helper-skills/1"))
        .andExpect(status().isNoContent());

        verify(helperSkillService).deleteHelperSkill(1);
    }

    @Test
    void deleteHelperSkill_notFound_returns404() throws Exception{
        when(helperSkillService.deleteHelperSkill(99)).thenReturn(false);

        mockMvc.perform(delete("/api/helper-skills/99"))
        .andExpect(status().isNotFound());
    }
}
