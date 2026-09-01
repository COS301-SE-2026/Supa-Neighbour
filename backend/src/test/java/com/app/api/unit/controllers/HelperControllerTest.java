package com.app.api.unit.controllers;

import com.app.api.controllers.HelperController;
import com.app.api.models.Badges;
import com.app.api.models.Helper;
import com.app.api.models.TaskType;
import com.app.api.models.User;
import com.app.api.services.HelperService;
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
class HelperControllerTest {

    @Mock
    private HelperService helperService;

    @InjectMocks
    private HelperController helperController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Helper helper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(helperController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();

        User user = new User();

        TaskType taskType = new TaskType();
        taskType.setTasktypeid(1);

        Badges badges = new Badges();
        badges.setBadgeid(7);

        helper = new Helper();
        helper.setHelperid(1);
        helper.setUserid(user);
        helper.setTaskTypeid(taskType);
        helper.setBadgeid(badges);
        helper.setHelperXp(50);
        helper.setAvailable(true);
    }

    @Test
    void getAllHelpers_ListOfHelpers() throws Exception {

        List<Helper> helpers = Arrays.asList(helper);
        when(helperService.getAllHelpers()).thenReturn(helpers);

        mockMvc.perform(get("/api/helpers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].helperid").value(1))
                .andExpect(jsonPath("$[0].helperXp").value(50));

        verify(helperService, times(1)).getAllHelpers();
    }

    @Test
    void getAllHelpers_WhenEmpty_EmptyList() throws Exception {

        when(helperService.getAllHelpers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/helpers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(helperService, times(1)).getAllHelpers();
    }

    @Test
    void getHelperById_ReturnHelper() throws Exception {

        when(helperService.getHelperById(1)).thenReturn(helper);

        mockMvc.perform(get("/api/helpers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.helperid").value(1))
                .andExpect(jsonPath("$.available").value(true));

        verify(helperService, times(1)).getHelperById(1);
    }

    @Test
    void getHelperById_WhenNotExisting_NotFound() throws Exception {

        when(helperService.getHelperById(999)).thenReturn(null);

        mockMvc.perform(get("/api/helpers/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(helperService, times(1)).getHelperById(999);
    }

    @Test
    void createHelper_CreatedHelper() throws Exception {

        when(helperService.saveHelper(any(Helper.class))).thenReturn(helper);

        mockMvc.perform(post("/api/helpers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(helper)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.helperid").value(1))
                .andExpect(jsonPath("$.helperXp").value(50));

        verify(helperService, times(1)).saveHelper(any(Helper.class));
    }

    @Test
    void updateHelper_WhenExists() throws Exception {

        Helper updatedHelper = new Helper();
        updatedHelper.setHelperid(1);
        updatedHelper.setHelperXp(90);
        updatedHelper.setAvailable(false);

        when(helperService.getHelperById(1)).thenReturn(helper);
        when(helperService.updateHelper(eq(1), any(Helper.class))).thenReturn(updatedHelper);

        mockMvc.perform(put("/api/helpers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedHelper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.helperXp").value(90))
                .andExpect(jsonPath("$.available").value(false));

        verify(helperService, times(1)).getHelperById(1);
        verify(helperService, times(1)).updateHelper(eq(1), any(Helper.class));
    }

    @Test
    void updateHelper_WhenNotExists() throws Exception {

        when(helperService.getHelperById(999)).thenReturn(null);

        mockMvc.perform(put("/api/helpers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(helper)))
                .andExpect(status().isNotFound());

        verify(helperService, times(1)).getHelperById(999);
        verify(helperService, never()).updateHelper(anyInt(), any(Helper.class));
    }

    @Test
    void deleteHelper_WhenExisting() throws Exception {

        when(helperService.getHelperById(1)).thenReturn(helper);
        doNothing().when(helperService).deleteHelper(1);

        mockMvc.perform(delete("/api/helpers/1"))
                .andExpect(status().isNoContent());

        verify(helperService, times(1)).getHelperById(1);
        verify(helperService, times(1)).deleteHelper(1);
    }

    @Test
    void deleteHelper_WhenNotExisting() throws Exception {

        when(helperService.getHelperById(999)).thenReturn(null);

        mockMvc.perform(delete("/api/helpers/999"))
                .andExpect(status().isNotFound());

        verify(helperService, times(1)).getHelperById(999);
        verify(helperService, never()).deleteHelper(anyInt());
    }

    @Test
    void getAllAvailableHelpers_ReturnAvailableHelpers() throws Exception {

        List<Helper> helpers = Arrays.asList(helper);
        when(helperService.findAllByStatus(true)).thenReturn(helpers);

        mockMvc.perform(get("/api/helpers/available")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(helperService, times(1)).findAllByStatus(true);
    }

    @Test
    void getAllAvailableHelpers_WhenEmpty_EmptyList() throws Exception {

        when(helperService.findAllByStatus(true)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/helpers/available")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(helperService, times(1)).findAllByStatus(true);
    }
}