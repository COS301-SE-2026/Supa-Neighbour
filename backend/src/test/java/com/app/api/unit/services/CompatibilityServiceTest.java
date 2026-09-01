package com.app.api.unit.services;

import com.app.api.models.Compatibility;
import com.app.api.models.Dependent;
import com.app.api.models.Helper;
import com.app.api.repositories.CompatibilityRepository;
import com.app.api.services.CompatibilityService;

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

public class CompatibilityServiceTest {

    @Mock
    private CompatibilityRepository compatibilityRepository;

    @InjectMocks
    private CompatibilityService compatibilityService;

    private Helper mockHelper;
    private Dependent mockDependent;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);

        mockHelper = new Helper();
        mockHelper.setHelperid(1);

        mockDependent = new Dependent();
        mockDependent.setDependentId(1);
    }

    // All tests follow the AAA pattern (Arrange, Act, Assert) and are designed to be independent of each other.

    @Test
    void getAllCompatibility_ReturnAllCompatibility() {

        Compatibility compatibility1 = new Compatibility();
        compatibility1.setCompatibilityid(1);
        compatibility1.setCompatibilityScore(80);

        Compatibility compatibility2 = new Compatibility();
        compatibility2.setCompatibilityid(2);
        compatibility2.setCompatibilityScore(60);

        List<Compatibility> compatibilities = Arrays.asList(compatibility1, compatibility2);
        when(compatibilityRepository.findAll()).thenReturn(compatibilities);

        List<Compatibility> result = compatibilityService.getAllCompatibility();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getCompatibilityid());
        verify(compatibilityRepository, times(1)).findAll();
    }

    @Test
    void getAllCompatibility_ReturnEmptyList() {

        when(compatibilityRepository.findAll()).thenReturn(List.of());

        List<Compatibility> result = compatibilityService.getAllCompatibility();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(compatibilityRepository, times(1)).findAll();
    }

    @Test
    void getCompatibilityById_ReturnCompatibility() {

        int id = 1;
        Compatibility compatibility = new Compatibility();
        compatibility.setCompatibilityid(id);
        compatibility.setCompatibilityScore(75);

        when(compatibilityRepository.findById(id)).thenReturn(Optional.of(compatibility));

        Compatibility result = compatibilityService.getCompatibilityById(id);

        assertNotNull(result);
        assertEquals(id, result.getCompatibilityid());
        assertEquals(75, result.getCompatibilityScore());
        verify(compatibilityRepository, times(1)).findById(id);
    }

    @Test
    void getCompatibilityById_ReturnNull() {

        int id = 999;
        when(compatibilityRepository.findById(id)).thenReturn(Optional.empty());

        Compatibility result = compatibilityService.getCompatibilityById(id);

        assertNull(result);
        verify(compatibilityRepository, times(1)).findById(id);
    }

    @Test
    void saveCompatibility_SaveAndReturnCompatibility() {

        Compatibility compatibility = new Compatibility();
        compatibility.setCompatibilityid(1);
        compatibility.setHelperid(mockHelper);
        compatibility.setDependentid(mockDependent);
        compatibility.setCompatibilityScore(90);
        compatibility.setCompatibilityColour("Green");

        when(compatibilityRepository.save(compatibility)).thenReturn(compatibility);

        Compatibility result = compatibilityService.saveCompatibility(compatibility);

        assertNotNull(result);
        assertEquals(1, result.getCompatibilityid());
        assertEquals(mockHelper, result.getHelperid());
        assertEquals(mockDependent, result.getDependentid());
        assertEquals(90, result.getCompatibilityScore());
        assertEquals("Green", result.getCompatibilityColour());
        verify(compatibilityRepository, times(1)).save(compatibility);
    }

    @Test
    void saveCompatibility_CompatibilityIsNull() {

        Compatibility result = compatibilityService.saveCompatibility(null);

        assertNull(result);
        verify(compatibilityRepository, never()).save(any(Compatibility.class));
    }

    @Test
    void updateCompatibility_CompatibilityExists() {

        int id = 1;

        Compatibility existing = new Compatibility();
        existing.setCompatibilityid(id);
        existing.setHelperid(mockHelper);
        existing.setDependentid(mockDependent);
        existing.setCompatibilityScore(50);
        existing.setCompatibilityColour("Yellow");

        Helper newHelper = new Helper();
        newHelper.setHelperid(5);

        Dependent newDependent = new Dependent();
        newDependent.setDependentId(8);

        Compatibility updates = new Compatibility();
        updates.setHelperid(newHelper);
        updates.setDependentid(newDependent);
        updates.setCompatibilityScore(95);
        updates.setCompatibilityColour("Green");

        when(compatibilityRepository.findById(id)).thenReturn(Optional.of(existing));
        when(compatibilityRepository.save(existing)).thenReturn(existing);

        Compatibility result = compatibilityService.updateCompatibility(id, updates);

        assertNotNull(result);
        assertEquals(95, result.getCompatibilityScore());
        assertEquals("Green", result.getCompatibilityColour());
        assertEquals(5, result.getHelperid().getHelperid());
        assertEquals(8, result.getDependentid().getDependentId());
        verify(compatibilityRepository, times(1)).save(existing);
    }

    @Test
    void updateCompatibility_CompatibilityDoesNotExist() {

        int id = 999;
        Compatibility updates = new Compatibility();
        updates.setCompatibilityScore(95);

        when(compatibilityRepository.findById(id)).thenReturn(Optional.empty());

        Compatibility result = compatibilityService.updateCompatibility(id, updates);

        assertNull(result);
        verify(compatibilityRepository, never()).save(any(Compatibility.class));
    }

    @Test
    void deleteCompatibility_DeleteCompatibility() {

        int id = 1;
        doNothing().when(compatibilityRepository).deleteById(id);

        compatibilityService.deleteCompatibility(id);

        verify(compatibilityRepository, times(1)).deleteById(id);
    }
}