package com.app.api.unit.services;

import com.app.api.models.Availability;
import com.app.api.repositories.AvailabilityRepository;
import com.app.api.services.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    private AvailabilityService availabilityService;

    @BeforeEach
    void setUp() {
        availabilityService = new AvailabilityService(availabilityRepository);
    }

    @Test
    void getAllAvailability_returnAllRecords() {
        Availability a1 = new Availability();
        a1.setAvailabilityid(1);

        Availability a2 = new Availability();
        a2.setAvailabilityid(2);

        when(availabilityRepository.findAll()).thenReturn(List.of(a1, a2));

        List<Availability> result = availabilityService.getAllAvailability();

        assertEquals(2, result.size());
        assertEquals(a1, result.get(0));
        assertEquals(a2, result.get(1));
    }

    @Test
    void getAvailabilityById_whenFound_returnsAvailability() {
        Availability actual = new Availability();
        actual.setAvailabilityid(1);

        when(availabilityRepository.findById(1)).thenReturn(Optional.of(actual));

        Availability result = availabilityService.getAvailabilityById(1);
        assertEquals(actual, result);
    }

    @Test
    void getAvailabilityById_whenNotFound_returnsNull() {

        when(availabilityRepository.findById(1)).thenReturn(Optional.empty());

        Availability result = availabilityService.getAvailabilityById(1);
        assertNull(result);
    }

    @Test
    void saveAvailability_whenNull_returnsNull() {
        Availability result = availabilityService.saveAvailability(null);
        assertNull(result);
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void saveAvailability_whenValid_returnsSavedAvailability() {
        Availability availability = new Availability();
        availability.setAvailabilityid(1);
        availability.setDayofweek("MONDAY");

        when(availabilityRepository.save(availability)).thenReturn(availability);

        Availability actual = availabilityService.saveAvailability(availability);

        assertEquals(availability, actual);
        assertEquals(1, actual.getAvailabilityid());
        assertEquals("MONDAY", actual.getDayofweek());

        verify(availabilityRepository).save(availability);
    }

    @Test
    void updateAvailability_whenFound_updatesAndReturnsAvailability() {

        Availability existing = new Availability();
        existing.setAvailabilityid(1);
        existing.setDayofweek("MONDAY");
        existing.setTimewindow("09:00");
        existing.setIsactive(true);

        Availability updated = new Availability();
        updated.setDayofweek("TUESDAY");
        updated.setTimewindow("10:00");
        updated.setIsactive(false);

        when(availabilityRepository.findById(1)).thenReturn(Optional.of(existing));
        when(availabilityRepository.save(existing)).thenReturn(existing);

        Availability actual = availabilityService.updateAvailability(1, updated);

        assertEquals(existing, actual);
        assertEquals("TUESDAY", actual.getDayofweek());
        assertEquals("10:00", actual.getTimewindow());
        assertFalse(existing.isIsactive());
        verify(availabilityRepository).save(existing);
    }

    @Test
    void updateAvailability_whenNotFound_returnsNull() {
        Availability updated = new Availability();
        updated.setDayofweek("TUESDAY");

        when(availabilityRepository.findById(1)).thenReturn(Optional.empty());

        Availability result = availabilityService.updateAvailability(1, updated);
        assertNull(result);
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void deleteAvailability_whenFound_deletesAvailabilityTrue() {
        when(availabilityRepository.existsById(1)).thenReturn(true);

        boolean result = availabilityService.deleteAvailability(1);

        assertTrue(result);
        verify(availabilityRepository).deleteById(1);
    }

    @Test
    void deleteAvailability_whenNotFound_returnsFalse() {
        when(availabilityRepository.existsById(1)).thenReturn(false);

        boolean result = availabilityService.deleteAvailability(1);
        assertFalse(result);
        verify(availabilityRepository, never()).deleteById(anyInt());
    }
}