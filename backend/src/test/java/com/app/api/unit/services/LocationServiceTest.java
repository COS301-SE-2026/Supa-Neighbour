package com.app.api.unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.api.models.Location;
import com.app.api.repositories.LocationRepository;
import com.app.api.services.LocationService;


@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private Location sample;
    @BeforeEach
    void setUp(){
        sample = Location.builder()
        .locationid(1)
        .locationCenterPoint(100)
        .locationRadius(500)
        .neighbourhoodid(10)
        .neighbourhoodName("Willow Park")
        .build();
    }

    @Test
    void getAllLocations_returnsAllRecords(){
        when(locationRepository.findAll()).thenReturn(List.of(sample));

        List<Location> result = locationService.getAllLocations();

        assertEquals(1, result.size());
        assertEquals(sample, result.get(0));
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void getAllLocations_noRecords_returnsEmptyList(){
        when(locationRepository.findAll()).thenReturn(List.of());
        List<Location> result = locationService.getAllLocations();

        assertTrue(result.isEmpty());
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void getLocationById_found_returndRecord(){
        when(locationRepository.findById(1)).thenReturn(Optional.of(sample));
        Location result = locationService.getLocationById(1);
        assertEquals(sample, result);
        verify(locationRepository).findById(1);
    }

    @Test
    void getLocationById_notFound_returnaNull(){
        when(locationRepository.findById(99)).thenReturn(Optional.empty());

        Location result = locationService.getLocationById(99);
        verify(locationRepository).findById(99);
    }

    @Test
    void saveLocation_validRecord_savesAndReturns(){
        when(locationRepository.save(sample)).thenReturn(sample);

        Location result = locationService.saveLocation(sample);
        assertEquals(sample, result);
        verify(locationRepository).save(sample);
    }

    @Test
    void saveLocation_null_returnsNullWIthoutCallingReposiotry(){
        Location result = locationService.saveLocation(null);

        assertNull(result);
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void updateLocation_found_updatedFieldsAndSaves(){
        Location existing = Location.builder()
        .locationid(1)
        .locationCenterPoint(100)
        .locationRadius(500)
        .neighbourhoodid(10)
        .neighbourhoodName("Willow Park")
        .build();

        Location updated = Location.builder()
        .locationid(1)
        .locationCenterPoint(200)
        .locationRadius(750)
        .neighbourhoodid(20)
        .neighbourhoodName("Willow Park East")
        .build();

        when(locationRepository.findById(1)).thenReturn(Optional.of(existing));
        when(locationRepository.save(existing)).thenReturn(existing);

        Location result = locationService.updateLocation(1, updated);
        
        assertEquals(existing, result);
        assertEquals(200, existing.getLocationCenterPoint());
        assertEquals(750, existing.getLocationRadius());
        assertEquals(20, existing.getNeighbourhoodid());
        assertEquals("Willow Park East", existing.getNeighbourhoodName());

        verify(locationRepository).findById(1);
        verify(locationRepository).save(existing);
    }

    @Test
    void updateLocation_notFound_returnsNullWithoutSaving(){
        when(locationRepository.findById(99)).thenReturn(Optional.empty());

        Location result = locationService.updateLocation(99, sample);

        assertNull(result);
        verify(locationRepository).findById(99);
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void deleteLocation_callsRepositoryDeleteById(){
        locationService.deleteLocation(1);

        verify(locationRepository, times(1)).deleteById(1);
    }



    
}
