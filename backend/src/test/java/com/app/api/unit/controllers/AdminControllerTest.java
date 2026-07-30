package com.app.api.unit.controllers;
import com.app.api.controllers.AdminController;
import com.app.api.dtos.ModeResponse;
import com.app.api.dtos.ShowStatusRequest;
import com.app.api.dtos.ShowStatusResponse;
import com.app.api.dtos.UserStatusResponse;
import com.app.api.repositories.SettingsRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.SettingsServices;
import com.app.api.controllers.SettingsController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import com.app.api.repositories.AdminRepository;
import com.app.api.services.AdminService;
import java.util.List;
import com.app.api.models.Admin;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminService adminServices;

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AdminRepository adminRepository;

    private static final String VALID_TOKEN = "Bearer valid-token";

    @Test
    void getAdmins_withValidToken_returns200() throws Exception{
        when(adminServices.getAllAdmins()).thenReturn(List.of(new Admin()));

        mockMvc.perform(get("/api/admins")).andExpect(status().isOk());
    }

    // @Test
    // void getAdmins_withInValidToken_returns401() throws Exception {
    //     when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));

    //     mockMvc.perform(get("/api/admins")
    //         .header("Authorization", "Bearer bad-token"))
    //         .andExpect(status().isUnauthorized());
    // }

    @Test
    void getAdminById_withValidToken_returns200() throws Exception {
        Admin admin = new Admin();

        when(adminServices.getAdminById(1)).thenReturn(admin);

        mockMvc.perform(get("/api/admins/1")).andExpect(status().isOk());
    }

    // @Test
    // void getAdminById_withInvalidToken_return404() throws Exception {
    //     when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));
        
    //     mockMvc.perform(get("/api/admins/1").header("Authorization", "Bearer bad-token"))
    //     .andExpect(status().isUnauthorized());
    // }

    @Test
    void createAdmin_withValidToken_return201() throws Exception {
        Admin admin = new Admin();
        when(adminServices.saveAdmin(any(Admin.class))).thenReturn(admin);

        mockMvc.perform(post("/api/admins")
        .contentType("application/json").content(objectMapper.writeValueAsString(admin)))
        .andExpect(status().isCreated());
    }

    // @Test
    // void createAdmin_withInvalidToken_return401() throws Exception{
    //             when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));
        
    //     mockMvc.perform(post("/api/admins").header("Authorization", "Bearer bad-token"))
    //     .andExpect(status().isUnauthorized());
    // }

    @Test
    void updateAdmin_withValidToken_return200() throws Exception {
        Admin existing = new Admin();
        Admin update = new Admin();

        when(adminServices.getAdminById(1)).thenReturn(existing);
        when(adminServices.updateAdmin(any(Integer.class),any(Admin.class))).thenReturn(update);

        mockMvc.perform(put("/api/admins/1")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(update))).andExpect(status().isOk());
    }

    // @Test
    // void updateAdmin_withInvalidToken_return401() throws Exception {
    //     when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));
        
    //     mockMvc.perform(put("/api/admins/1")
    //     .header("Authorization", "Bearer bad-token"))
    //     .andExpect(status().isUnauthorized());
    // }

    @Test
    void deleteAdmin_withValidToken_return204() throws Exception {
        
        Admin admin = new Admin();

        when(adminServices.getAdminById(1)).thenReturn(admin);
        
        mockMvc.perform(delete("/api/admins/1")).andExpect(status().isNoContent());

        verify(adminServices).deleteAdmin(1);
    }

    // @Test
    // void deleteAmind_withInvalidToken_return401() throws Exception {
    //     when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));
        
    //     mockMvc.perform(delete("/api/admins/1")
    //     .header("Authorization", "Bearer bad-token"))
    //     .andExpect(status().isUnauthorized());
    // }
}
//Get /api/admins
//Get /api/admins/1
//Put /api/admins/1
//Delete /api/admmins/1
