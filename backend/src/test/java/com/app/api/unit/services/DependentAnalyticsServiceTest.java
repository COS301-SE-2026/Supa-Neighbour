package com.app.api.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.api.models.DependentAnalytics;
import com.app.api.models.Location;
import com.app.api.models.TaskType;
import com.app.api.models.User;
import com.app.api.repositories.DependentAnalyticsRepository;
import com.app.api.services.DependentAnalyticsService;

@ExtendWith(MockitoExtension.class)
class DependentAnalyticsServiceTest {

    @Mock
    private DependentAnalyticsRepository dependentAnalyticsRepository;

    @InjectMocks
    private DependentAnalyticsService dependentAnalyticsService;

    private DependentAnalytics analytics;
    private DependentAnalytics analytics2;

    private User user;
    private TaskType taskType;
    private Location location;

    @BeforeEach
    void setUp() {

        user = mock(User.class);
        taskType = mock(TaskType.class);
        location = mock(Location.class);

        analytics = new DependentAnalytics(
                "analytics-1",
                user,
                taskType,
                10,
                location,
                4.5f,
                4.0f
        );

        analytics2 = new DependentAnalytics(
                "analytics-2",
                user,
                taskType,
                20,
                location,
                4.2f,
                3.8f
        );
    }


    @Test
    void getAllDependentAnalytics_shouldReturnAllRecords() {

        List<DependentAnalytics> expected =
                Arrays.asList(analytics, analytics2);

        when(dependentAnalyticsRepository.findAll())
                .thenReturn(expected);

        List<DependentAnalytics> result =
                dependentAnalyticsService.getAllDependentAnalytics();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expected, result);

        verify(dependentAnalyticsRepository, times(1))
                .findAll();
    }

    @Test
    void getDependentAnalyticsById_shouldReturnRecordWhenFound() {

        when(dependentAnalyticsRepository.findById("analytics-1"))
                .thenReturn(Optional.of(analytics));

        DependentAnalytics result =
                dependentAnalyticsService
                        .getDependentAnalyticsById("analytics-1");

        assertNotNull(result);
        assertEquals("analytics-1", result.getDependentanalyticsid());

        verify(dependentAnalyticsRepository, times(1))
                .findById("analytics-1");
    }

    @Test
    void getDependentAnalyticsById_shouldReturnNullWhenNotFound() {

        when(dependentAnalyticsRepository.findById("does-not-exist"))
                .thenReturn(Optional.empty());

        DependentAnalytics result =
                dependentAnalyticsService
                        .getDependentAnalyticsById("does-not-exist");

        assertNull(result);

        verify(dependentAnalyticsRepository, times(1))
                .findById("does-not-exist");
    }

    @Test
    void getDependentAnalyticsById_shouldReturnNullWhenIdIsNull() {

        DependentAnalytics result =
                dependentAnalyticsService
                        .getDependentAnalyticsById(null);

        assertNull(result);

        verify(dependentAnalyticsRepository, never())
                .findById(anyString());
    }

    @Test
    void saveDependentAnalytics_shouldSaveRecord() {

        when(dependentAnalyticsRepository.save(analytics))
                .thenReturn(analytics);

        DependentAnalytics result =
                dependentAnalyticsService
                        .saveDependentAnalytics(analytics);

        assertNotNull(result);
        assertEquals(analytics, result);

        verify(dependentAnalyticsRepository, times(1))
                .save(analytics);
    }

    @Test
    void saveDependentAnalytics_shouldReturnNullWhenObjectIsNull() {

        DependentAnalytics result =
                dependentAnalyticsService
                        .saveDependentAnalytics(null);

        assertNull(result);

        verify(dependentAnalyticsRepository, never())
                .save(any());
    }

    @Test
    void updateDependentAnalytics_shouldUpdateExistingRecord() {

        DependentAnalytics updated = new DependentAnalytics(
                "analytics-1",
                user,
                taskType,
                50,
                location,
                4.9f,
                4.7f
        );

        when(dependentAnalyticsRepository.findById("analytics-1"))
                .thenReturn(Optional.of(analytics));

        when(dependentAnalyticsRepository.save(analytics))
                .thenReturn(analytics);

        DependentAnalytics result =
                dependentAnalyticsService
                        .updateDependentAnalytics(
                                "analytics-1",
                                updated
                        );

        assertNotNull(result);

        assertEquals(50, result.getTotaltasks());
        assertEquals(4.9f, result.getAveeragerating());
        assertEquals(4.7f, result.getAveragegivingrating());

        assertEquals(user, result.getUserid());
        assertEquals(taskType, result.getTasktypeid());
        assertEquals(location, result.getLocationid());

        verify(dependentAnalyticsRepository, times(1))
                .findById("analytics-1");

        verify(dependentAnalyticsRepository, times(1))
                .save(analytics);
    }

    @Test
    void updateDependentAnalytics_shouldReturnNullWhenIdIsNull() {

        DependentAnalytics result =
                dependentAnalyticsService
                        .updateDependentAnalytics(null, analytics);

        assertNull(result);

        verify(dependentAnalyticsRepository, never())
                .findById(anyString());

        verify(dependentAnalyticsRepository, never())
                .save(any());
    }

    @Test
    void updateDependentAnalytics_shouldReturnNullWhenUpdatedObjectIsNull() {

        DependentAnalytics result =
                dependentAnalyticsService
                        .updateDependentAnalytics("analytics-1", null);

        assertNull(result);

        verify(dependentAnalyticsRepository, never())
                .findById(anyString());

        verify(dependentAnalyticsRepository, never())
                .save(any());
    }

    @Test
    void updateDependentAnalytics_shouldReturnNullWhenRecordDoesNotExist() {

        when(dependentAnalyticsRepository.findById("does-not-exist"))
                .thenReturn(Optional.empty());

        DependentAnalytics result =
                dependentAnalyticsService
                        .updateDependentAnalytics(
                                "does-not-exist",
                                analytics
                        );

        assertNull(result);

        verify(dependentAnalyticsRepository, times(1))
                .findById("does-not-exist");

        verify(dependentAnalyticsRepository, never())
                .save(any());
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------

    @Test
    void deleteDependentAnalytics_shouldDeleteRecord() {

        doNothing()
                .when(dependentAnalyticsRepository)
                .deleteById("analytics-1");

        dependentAnalyticsService
                .deleteDependentAnalytics("analytics-1");

        verify(dependentAnalyticsRepository, times(1))
                .deleteById("analytics-1");
    }

    @Test
    void deleteDependentAnalytics_shouldDoNothingWhenIdIsNull() {

        dependentAnalyticsService
                .deleteDependentAnalytics(null);

        verify(dependentAnalyticsRepository, never())
                .deleteById(anyString());
    }
}

