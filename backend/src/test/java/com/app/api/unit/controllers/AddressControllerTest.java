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

import com.app.api.controllers.AddressController;
import com.app.api.models.Address;
import com.app.api.repositories.UserRepository;
import com.app.api.services.AddressService;
import com.app.api.services.FirebaseAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AddressControllerTest {
    
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
    private AddressService addressServices;

    @Test
    void getAddresses_withValidToken_returns200() throws Exception{
        when(addressServices.getAllAddresses()).thenReturn(List.of(new Address()));

        mockMvc.perform(get("/api/addresses")).andExpect(status().isOk());
    }

    @Test
    void getAddressById_withValidToken_returns200() throws Exception {
        Address address = new Address();

        when(addressServices.getAddressById(1)).thenReturn(address);

        mockMvc.perform(get("/api/addresses/1")).andExpect(status().isOk());
    }

    @Test
    void createAddress_withValidToken_return201() throws Exception {
        Address address = new Address();
        when(addressServices.saveAddress(any(Address.class))).thenReturn(address);

        mockMvc.perform(post("/api/addresses")
        .contentType("application/json").content(objectMapper.writeValueAsString(address)))
        .andExpect(status().isCreated());
    }

    @Test
    void updateAddress_withValidToken_return200() throws Exception {
        Address existing = new Address();
        Address update = new Address();

        when(addressServices.getAddressById(1)).thenReturn(existing);
        when(addressServices.updateAddress(any(Integer.class),any(Address.class))).thenReturn(update);

        mockMvc.perform(put("/api/addresses/1")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(update))).andExpect(status().isOk());
    }


    @Test
    void deleteAddress_withValidToken_return204() throws Exception {
        
        Address address = new Address();

        when(addressServices.getAddressById(1)).thenReturn(address);
        
        mockMvc.perform(delete("/api/addresses/1")).andExpect(status().isNoContent());

        verify(addressServices).deleteAddress(1);
    }

}
