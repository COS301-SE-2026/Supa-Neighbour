package com.app.api.unit.services;

import com.app.api.models.Compatibility;
import com.app.api.models.HelperAnalytics;
import com.app.api.models.Location;
import com.app.api.models.TaskType;
import com.app.api.models.User;
import com.app.api.repositories.HelperAnalyticsRepository;
import com.app.api.services.HelperAnalyticsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HelperAnalyticsServiceTest {

    @Mock
    private HelperAnalyticsRepository helperAnalyticsRepository;

    @InjectMocks
    private HelperAnalyticsService helperAnalyticsService;

    private User mockUser;
    private TaskType mockTaskType;
    private Location mockLocation;
    private Compatibility mockCompatibility;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();

        mockTaskType = new TaskType();
        mockTaskType.setTasktypeid(1);

        mockLocation = new Location();
        mockLocation.setLocationid(1);

        mockCompatibility = new Compatibility();
        mockCompatibility.setCompatibilityid(1);
    }

    // All tests follow the AAA pattern (Arrange, Act, Assert) and are designed to be independent of each other.

    @Test
    void getAllHelperAnalytics_ReturnAllHelperAnalytics() {

        HelperAnalytics analytics1 = new HelperAnalytics();
        analytics1.setHelperAnalyticsid("HA1");
        analytics1.setAverageRating(4.5f);

        HelperAnalytics analytics2 = new HelperAnalytics();
        analytics2.setHelperAnalyticsid("HA2");
        analytics2.setAverageRating(3.8f);

        List<HelperAnalytics> analytics = Arrays.asList(analytics1, analytics2);
        when(helperAnalyticsRepository.findAll()).thenReturn(analytics);

        List<HelperAnalytics> result = helperAnalyticsService.getAllHelperAnalytics();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("HA1", result.get(0).getHelperAnalyticsid());
        verify(helperAnalyticsRepository, times(1)).findAll();
    }

    @Test
    void getAllHelperAnalytics_ReturnEmptyList() {

        when(helperAnalyticsRepository.findAll()).thenReturn(List.of());

        List<HelperAnalytics> result = helperAnalyticsService.getAllHelperAnalytics();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(helperAnalyticsRepository, times(1)).findAll();
    }

    @Test
    void getHelperAnalyticsById_ReturnHelperAnalytics() {

        String id = "HA1";
        HelperAnalytics analytics = new HelperAnalytics();
        analytics.setHelperAnalyticsid(id);
        analytics.setAverageRating(4.2f);

        when(helperAnalyticsRepository.findById(id)).thenReturn(Optional.of(analytics));

        HelperAnalytics result = helperAnalyticsService.getHelperAnalyticsById(id);

        assertNotNull(result);
        assertEquals(id, result.getHelperAnalyticsid());
        assertEquals(4.2f, result.getAverageRating());
        verify(helperAnalyticsRepository, times(1)).findById(id);
    }

    @Test
    void getHelperAnalyticsById_ReturnNull() {

        String id = "UNKNOWN";
        when(helperAnalyticsRepository.findById(id)).thenReturn(Optional.empty());

        HelperAnalytics result = helperAnalyticsService.getHelperAnalyticsById(id);

        assertNull(result);
        verify(helperAnalyticsRepository, times(1)).findById(id);
    }

    @Test
    void getHelperAnalyticsById_IdIsNull() {

        HelperAnalytics result = helperAnalyticsService.getHelperAnalyticsById(null);

        assertNull(result);
        verify(helperAnalyticsRepository, never()).findById(any());
    }

    @Test
    void saveHelperAnalytics_SaveAndReturnHelperAnalytics() {

        HelperAnalytics analytics = new HelperAnalytics();
        analytics.setHelperAnalyticsid("HA1");
        analytics.setUserid(mockUser);
        analytics.setTasktypeid(mockTaskType);
        analytics.setLocationid(mockLocation);
        analytics.setAverageRating(4.0f);

        when(helperAnalyticsRepository.save(analytics)).thenReturn(analytics);

        HelperAnalytics result = helperAnalyticsService.saveHelperAnalytics(analytics);

        assertNotNull(result);
        assertEquals("HA1", result.getHelperAnalyticsid());
        assertEquals(mockUser, result.getUserid());
        assertEquals(mockTaskType, result.getTasktypeid());
        assertEquals(mockLocation, result.getLocationid());
        assertEquals(4.0f, result.getAverageRating());
        verify(helperAnalyticsRepository, times(1)).save(analytics);
    }

    @Test
    void saveHelperAnalytics_HelperAnalyticsIsNull() {

        HelperAnalytics result = helperAnalyticsService.saveHelperAnalytics(null);

        assertNull(result);
        verify(helperAnalyticsRepository, never()).save(any(HelperAnalytics.class));
    }

    @Test
    void updateHelperAnalytics_HelperAnalyticsExists() {

        String id = "HA1";

        HelperAnalytics existing = new HelperAnalytics();
        existing.setHelperAnalyticsid(id);
        existing.setUserid(mockUser);
        existing.setTasktypeid(mockTaskType);
        existing.setLocationid(mockLocation);
        existing.setAverageRating(2.0f);

        User newUser = new User();

        TaskType newTaskType = new TaskType();
        newTaskType.setTasktypeid(3);

        Location newLocation = new Location();
        newLocation.setLocationid(4);

        Compatibility newCompatibility = new Compatibility();
        newCompatibility.setCompatibilityid(5);

        HelperAnalytics updates = new HelperAnalytics();
        updates.setUserid(newUser);
        updates.setTasktypeid(newTaskType);
        updates.setLocationid(newLocation);
        updates.setAverageRating(4.9f);

        when(helperAnalyticsRepository.findById(id)).thenReturn(Optional.of(existing));
        when(helperAnalyticsRepository.save(existing)).thenReturn(existing);

        HelperAnalytics result = helperAnalyticsService.updateHelperAnalytics(id, updates);

        assertNotNull(result);
        assertEquals(newUser, result.getUserid());
        assertEquals(3, result.getTasktypeid().getTasktypeid());
        assertEquals(4, result.getLocationid().getLocationid());
        assertEquals(4.9f, result.getAverageRating());
        verify(helperAnalyticsRepository, times(1)).save(existing);
    }

    @Test
    void updateHelperAnalytics_HelperAnalyticsDoesNotExist() {

        String id = "UNKNOWN";
        HelperAnalytics updates = new HelperAnalytics();
        updates.setAverageRating(4.9f);

        when(helperAnalyticsRepository.findById(id)).thenReturn(Optional.empty());

        HelperAnalytics result = helperAnalyticsService.updateHelperAnalytics(id, updates);

        assertNull(result);
        verify(helperAnalyticsRepository, never()).save(any(HelperAnalytics.class));
    }

    @Test
    void updateHelperAnalytics_IdIsNull() {

        HelperAnalytics updates = new HelperAnalytics();
        updates.setAverageRating(4.9f);

        HelperAnalytics result = helperAnalyticsService.updateHelperAnalytics(null, updates);

        assertNull(result);
        verify(helperAnalyticsRepository, never()).findById(any());
        verify(helperAnalyticsRepository, never()).save(any(HelperAnalytics.class));
    }

    @Test
    void updateHelperAnalytics_UpdatedIsNull() {

        String id = "HA1";

        HelperAnalytics result = helperAnalyticsService.updateHelperAnalytics(id, null);

        assertNull(result);
        verify(helperAnalyticsRepository, never()).findById(any());
        verify(helperAnalyticsRepository, never()).save(any(HelperAnalytics.class));
    }

    @Test
    void deleteHelperAnalytics_DeleteHelperAnalytics() {

        String id = "HA1";
        doNothing().when(helperAnalyticsRepository).deleteById(id);

        helperAnalyticsService.deleteHelperAnalytics(id);

        verify(helperAnalyticsRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteHelperAnalytics_IdIsNull() {

        helperAnalyticsService.deleteHelperAnalytics(null);

        verify(helperAnalyticsRepository, never()).deleteById(any());
    }
}