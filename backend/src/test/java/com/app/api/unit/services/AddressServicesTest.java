package com.app.api.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import com.app.api.models.Location;
import com.app.api.models.Address;
import com.app.api.repositories.AddressRepository;
import com.app.api.services.AddressService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AddressServicesTest {
    
    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private Address address;
    private Location location;


    @BeforeEach
    void setUp() {
        location = new Location();
        address = new Address();

        address.setAddressid(1);
        address.setStreet("street street");
        address.setStreetNumber(22);
        address.setZipcode(27);
        address.setNeighbourhoodid(location);
    }

    @Test
    void getAllAddress_returnList() {
        when(addressRepository.findAll()).thenReturn(List.of(address));
        List<Address> result = addressService.getAllAddresses();

        assertEquals(1, result.size());

        verify(addressRepository).findAll();
    }

    @Test
    void getAddressByID_returnAddress() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));

        Address result = addressService.getAddressById(1);

        assertNotNull(result);
        assertEquals(1, result.getAddressid());
        assertEquals("street street",result.getStreet());

        verify(addressRepository).findById(1);
    }

    @Test
    void saveAddress_validAddress_returnSavedAddress() {
        when(addressRepository.save(address)).thenReturn(address);

        Address result = addressService.saveAddress(address);

        assertNotNull(result);
        assertEquals(address, result);

        verify(addressRepository).save(address);
    }

    @Test
    void updateAddress_returnAddress() {
        Address updated = new Address();
        updated.setStreet("Second Street");
        updated.setStreetNumber(20);
        updated.setZipcode(9000);
        Location newLocation = new Location();

        updated.setNeighbourhoodid(newLocation);

        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class)))
        .thenAnswer(invocation ->invocation.getArgument(0));

        Address result= addressService.updateAddress(1, updated);

        assertNotNull(result);
        assertEquals("Second Street", result.getStreet());
        assertEquals(20, result.getStreetNumber());
        assertEquals(9000, result.getZipcode());
        assertEquals(newLocation, result.getNeighbourhoodid());

        verify(addressRepository).findById(1);
        verify(addressRepository).save(address);
    }

    @Test
    void deleteAddress_withValidAddress() {
        doNothing().when(addressRepository).deleteById(1);

        addressService.deleteAddress(1);

        verify(addressRepository).deleteById(1);
    }
}
