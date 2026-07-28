package com.app.api.unit.controllers;


import com.app.api.models.Address;
import com.app.api.models.Location;
import com.app.api.services.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.app.api.controllers.AddressController;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    value = AddressController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {com.app.api.security.FirebaseAuthenticationFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class AddressControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired 
    private ObjectMapper objectMapper;

    @MockitoBean
    private AddressService addressService;

    private Address sampleAddress;
    private Location sampleLocation;

    @BeforeEach
    void setUp(){


        reset(addressService);

        sampleLocation = new Location();
        sampleLocation.setLocationid(5);
        sampleLocation.setNeighbourhoodName("Test Neighbourhood");
        sampleLocation.setLocationRadius(10);
        sampleLocation.setLocationCenterPoint(100);
   
        sampleAddress = new Address();
        sampleAddress.setAddressid(1);
        sampleAddress.setStreet("Rissik Street");
        sampleAddress.setStreetNumber(22);
        sampleAddress.setZipcode(1000);
        sampleAddress.setNeighbourhoodid(sampleLocation);
        
    }

    @Test
    void getAllAddresses_returnsOkWithList()  throws Exception{
        when(addressService.getAllAddresses()).thenReturn(List.of(sampleAddress));

        mockMvc.perform(get("/api/addresses"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].addressid").value(1))
        .andExpect(jsonPath("$[0].street").value("Rissik Street"))
        .andExpect(jsonPath("$[0].streetNumber").value(22))
        .andExpect(jsonPath("$[0].zipcode").value(1000))
        .andExpect(jsonPath("$[0].neighbourhoodid.locationid").value(5)); 

        verify(addressService).getAllAddresses();
    }

    @Test
    void getAddressById_found_returnsOk() throws Exception{
        when(addressService.getAddressById(1)).thenReturn(sampleAddress);

        mockMvc.perform(get("/api/addresses/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.addressid").value(1))
        .andExpect(jsonPath("$.street").value("Rissik Street"))
        .andExpect(jsonPath("$.streetNumber").value(22))
        .andExpect(jsonPath("$.zipcode").value(1000))
        .andExpect(jsonPath("$.neighbourhoodid.locationid").value(5));
    }

    @Test
    void getAddressById_notFound_returns404() throws Exception{
        when(addressService.getAddressById(99)).thenReturn(null);

        mockMvc.perform(get("/api/addresses/99")).andExpect(status().isNotFound());
    }

    /*@Test
    void saveAddress_validAddress_savesAndReturns(){
        when(addressRepository.save(existing)).thenReturn(existing);

        Address result = addressService.saveAddress(existing);
        verify(addressRepository).save(existing);
    }*/

    @Test 
    void createAddress_returnsCreatedWithBody() throws Exception{
        when(addressService.saveAddress(any(Address.class))).thenReturn(sampleAddress);
        mockMvc.perform(post("/api/addresses")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(sampleAddress)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.addressid").value(1))
        .andExpect(jsonPath("$.street").value("Rissik Street"))
        .andExpect(jsonPath("$.neighbourhoodid.locationid").value(5));
        verify(addressService).saveAddress(any(Address.class));
    }

    @Test
    void updateAddress_existing_returnsOkWithUpdateBody() throws Exception{

        Location newLocation = new Location();
        newLocation.setLocationid(7);
        newLocation.setNeighbourhoodName("New Neighbourhood");

        Address updatedInput = new Address();
        updatedInput.setStreet("Church Street");
        updatedInput.setStreetNumber(100);
        updatedInput.setZipcode(0002);
        updatedInput.setNeighbourhoodid(newLocation);

        Address updatedResult = new Address();
        updatedResult.setAddressid(1);
        updatedResult.setStreet("Church Street");
        updatedResult.setStreetNumber(100);
        updatedResult.setZipcode(0002);
        updatedResult.setNeighbourhoodid(newLocation);

        
        when(addressService.getAddressById(1)).thenReturn(sampleAddress); 
        when(addressService.updateAddress(eq(1), any(Address.class))).thenReturn(updatedResult);

        mockMvc.perform(put("/api/addresses/1")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updatedInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.street").value("Church Street"))
            .andExpect(jsonPath("$.streetNumber").value(100))
            .andExpect(jsonPath("$.zipcode").value(2))
            .andExpect(jsonPath("$.neighbourhoodid.locationid").value(7)); 
    }

    @Test
    void updateAddress_notFound_returns404() throws Exception{
        when(addressService.getAddressById(99)).thenReturn(null);

        mockMvc.perform(put("/api/addresses/99")
            .contentType("application/json")
        .content(objectMapper.writeValueAsString(sampleAddress)))
        .andExpect(status().isNotFound());
        verify(addressService, never()).updateAddress(anyInt(), any(Address.class));
    }

    @Test
    void deleteAddress_existing_returnsNoContent() throws Exception{
        when(addressService.getAddressById(1)).thenReturn(sampleAddress);

        mockMvc.perform(delete("/api/addresses/1"))
        .andExpect(status().isNoContent());
        verify(addressService).deleteAddress(1);
    }

    @Test
    void deleteAddress_notFount_returns404() throws Exception{
        when(addressService.getAddressById(99)).thenReturn(null);
        mockMvc.perform(delete("/api/addresses/99"))
        .andExpect(status().isNotFound());

        verify(addressService, never()).deleteAddress(anyInt());
    }
}
