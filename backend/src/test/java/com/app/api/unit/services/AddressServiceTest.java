package com.app.api.unit.services;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.api.models.Address;
import com.app.api.models.Location;
import com.app.api.repositories.AddressRepository;
import com.app.api.services.AddressService;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock 
    private AddressRepository addressRepository;

    private AddressService addressService;

    private Address existing;

    private Location sampleLocation;

    @BeforeEach
    void setup(){
        addressService = new AddressService(addressRepository);

        sampleLocation = new Location();
        sampleLocation.setLocationid(5);
        sampleLocation.setNeighbourhoodName("Test Neighbourhood");
        sampleLocation.setLocationRadius(10);
        sampleLocation.setLocationCenterPoint(100);

        existing = new Address();
        existing.setAddressid(1);
        existing.setStreet("Rissik Street");
        existing.setStreetNumber(22);
        existing.setZipcode(1000);
        existing.setNeighbourhoodid(sampleLocation);
    }

    @Test
    void getAllAdresses_returnsAllFromRepository(){
        when(addressRepository.findAll()).thenReturn(List.of(existing));
        List<Address> result = addressService.getAllAddresses();

        assertThat(result).hasSize(1).containsExactly(existing);
    }

    @Test
    void getAddressById_found_returnsAddress(){
        when(addressRepository.findById(1)).thenReturn(Optional.of(existing));

        Address result = addressService.getAddressById(1);
        assertThat(result).isEqualTo(existing);
    }

    @Test
    void getAdressById_notFound_returnsNull(){
        when(addressRepository.findById(99)).thenReturn(Optional.empty());
        Address result = addressService.getAddressById(99);

        assertThat(result).isNull();
    }

    @Test
    void saveAddress_validAddress_saveAndReturns(){
        when(addressRepository.save(existing)).thenReturn(existing);
        Address result = addressService.saveAddress(existing);
        assertThat(result).isEqualTo(existing);

        verify(addressRepository).save(existing);
    }

    @Test
    void saveAddress_nullAddress_returnsNullWithoutCallingRepository(){
        Address result = addressService.saveAddress(null);

        assertThat(result).isNull();

        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void updateAddress_existing_updatedFieldsAndSaves(){
        Location newLocation = new Location();
        newLocation.setLocationid(7);
        newLocation.setNeighbourhoodName("New Neighbourhood");
        newLocation.setLocationRadius(15);
        newLocation.setLocationCenterPoint(200);

        Address updateData = new Address();
        updateData.setStreet("Church Street");
        updateData.setZipcode(0002);
        updateData.setStreetNumber(200);
        updateData.setNeighbourhoodid(newLocation);
        
        when(addressRepository.findById(1)).thenReturn(Optional.of(existing));
        when(addressRepository.save(existing)).thenReturn(existing);

        Address result = addressService.updateAddress(1, updateData);
        assertThat(result.getStreet()).isEqualTo("Church Street");
        assertThat(result.getStreetNumber()).isEqualTo(200);
        assertThat(result.getZipcode()).isEqualTo(2);
        assertThat(result.getNeighbourhoodid()).isEqualTo(newLocation);
        assertThat(result.getNeighbourhoodid().getLocationid()).isEqualTo(7);

        verify(addressRepository).save(existing);
    }


    @Test
    void updateAddress_notFound_returnsNullWithoutSaving(){
        when(addressRepository.findById(99)).thenReturn(Optional.empty());
        Address result = addressService.updateAddress(99, existing);

        assertThat(result).isNull();
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void deleteAddress_callsRepositoryDeeleteById(){
        addressService.deleteAddress(1);

        verify(addressRepository).deleteById(1);
    }

    
}
