package com.app.api.unit.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.app.api.security.FirebaseAuthenticationFilter;
import java.util.List;
import com.app.api.models.Address;
import com.app.api.repositories.UserRepository;
import com.app.api.services.AddressService;
import com.app.api.services.BadgesService;
import com.app.api.services.FirebaseAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.api.controllers.BadgesController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.app.api.models.Badges;

@WebMvcTest(BadgesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BadgeControllerTest {
    
        
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BadgesService badgesServices;

    @Test
    void getAddresses_withValidToken_returns200() throws Exception{
        when(badgesServices.getAllBadges()).thenReturn(List.of(new Badges()));

        mockMvc.perform(get("/api/badges")).andExpect(status().isOk());
    }

    @Test
    void getBadgeById_withValidToken_returns200() throws Exception {
        Badges badges = new Badges();

        when(badgesServices.getBadgesById(1)).thenReturn(badges);

        mockMvc.perform(get("/api/badges/1")).andExpect(status().isOk());
    }

    @Test
    void createBadge_withValidToken_return201() throws Exception {
        Badges badge = new Badges();
        when(badgesServices.saveBadges(any(Badges.class))).thenReturn(badge);

        mockMvc.perform(post("/api/badges")
        .contentType("application/json").content(objectMapper.writeValueAsString(badge)))
        .andExpect(status().isCreated());
    }

    @Test
    void updateBadge_withValidToken_return200() throws Exception {
        Badges existing = new Badges();
        Badges update = new Badges();

        when(badgesServices.getBadgesById(1)).thenReturn(existing);
        when(badgesServices.updateBadges(any(Integer.class),any(Badges.class))).thenReturn(update);

        mockMvc.perform(put("/api/badges/1")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(update))).andExpect(status().isOk());
    }


    @Test
    void deleteBadge_withValidToken_return204() throws Exception {
        
        Badges badge = new Badges();

        when(badgesServices.getBadgesById(1)).thenReturn(badge);
        
        mockMvc.perform(delete("/api/badges/1")).andExpect(status().isNoContent());

        verify(badgesServices).deleteBadges(1);
    }

}
