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

import com.app.api.models.Analytics;
import com.app.api.repositories.AnalyticsRepository;
import com.app.api.services.AnalyticsService;


@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest{
    @Mock 
    private AnalyticsRepository analyticsRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Analytics sample;

    @BeforeEach
    void setUp(){
        sample = Analytics.builder().analyticsid(1).build();
    }

    @Test
    void getAllAnalytics_returnAllRecords(){
        when(analyticsRepository.findAll()).thenReturn(List.of(sample));

        List<Analytics> result = analyticsService.getAllAnalytics();

        assertEquals(1, result.size());
        assertEquals(sample, result.get(0));
        verify(analyticsRepository, times(1)).findAll();
    }

    @Test
    void getAllAnalytics_noRecords_returnsEmptyList(){
        when(analyticsRepository.findAll()).thenReturn(List.of());

        List<Analytics> result = analyticsService.getAllAnalytics();

        assertTrue(result.isEmpty());
        verify(analyticsRepository, times(1)).findAll();
    }

    @Test
    void getAnalyticsById_found_returnsRecord(){
        when(analyticsRepository.findById(1)).thenReturn(Optional.of(sample));

        Analytics result = analyticsService.getAnalyticsById(1);
        assertEquals(sample, result);
        verify(analyticsRepository).findById(1);
    }

    @Test
    void getAnalyticsById_notFound_returnsNull(){
        when(analyticsRepository.findById(99)).thenReturn(Optional.empty());

        Analytics result = analyticsService.getAnalyticsById(99);

        assertNull(result);
        verify(analyticsRepository).findById(99);
    }


    @Test
    void saveAnalytics_validRecord_savesAndReturns(){
        when(analyticsRepository.save(sample)).thenReturn(sample);
        Analytics result = analyticsService.saveAnalytics(sample);

        assertEquals(sample, result);
        verify(analyticsRepository).save(sample);
    }

    @Test
    void saveAnalytics_null_returnsNullWithoutCallingRepository(){
        Analytics result = analyticsService.saveAnalytics(null);

        assertNull(result);
        verify(analyticsRepository, never()).save(any(Analytics.class));
    }

    @Test
    void updateAnalytics_found_updatedFieldsAndSaves(){
        Analytics existing = Analytics.builder().analyticsid(1).build();

        Analytics updated = Analytics.builder().analyticsid(1).build();

        when(analyticsRepository.findById(1)).thenReturn(Optional.of(existing));
        when(analyticsRepository.save(existing)).thenReturn(existing);

        Analytics result = analyticsService.updateAnalytics(1, updated);

        assertEquals(existing, result);
        assertEquals(updated.getTaskid(), existing.getTaskid());
        assertEquals(updated.getAdminid(), existing.getAdminid());
        assertEquals(updated.getHelpertypeid(), existing.getHelpertypeid());
        assertEquals(updated.getDependenttypeid(), existing.getDependenttypeid());
        verify(analyticsRepository).findById(1);
        verify(analyticsRepository).save(existing);
    }

    @Test
    void updateAnalytics_notFpund_returnsNullWithoutSaving(){
        when(analyticsRepository.findById(99)).thenReturn(Optional.empty());

        Analytics result = analyticsService.updateAnalytics(99, sample);

        assertNull(result);
        verify(analyticsRepository).findById(99);
        verify(analyticsRepository, never()).save(any(Analytics.class));
    }

    @Test
    void deleteAnalytics_callsRepositoryDeleteById() {
        analyticsService.deleteAnalytics(1);

        verify(analyticsRepository, times(1)).deleteById(1);
    }
}
