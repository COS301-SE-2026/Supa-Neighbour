package com.app.api.unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import com.app.api.models.HelperSkill;
import com.app.api.repositories.HelperSkillRepository;
import com.app.api.services.HelperSkillService;

@ExtendWith(MockitoExtension.class)
public class HelperSkillServiceTest {

    @Mock
    private HelperSkillRepository helperSkillRepository;

    @InjectMocks
    private HelperSkillService helperSkillService;

    private HelperSkill sample;

    @BeforeEach
    void setUp(){
        sample = HelperSkill.builder().helperSkillId(1).build();
    }

    @Test
    void getAllHelpersSkills_returnsAllRecords(){
        when(helperSkillRepository.findAll()).thenReturn(List.of(sample));

        
        List<HelperSkill> result = helperSkillService.getAllHelpersSkills();
        
        assertEquals(1, result.size());
        assertEquals(sample, result.get(0));
        verify(helperSkillRepository, times(1)).findAll();
    }

    @Test
    void getAllHelperSkills_noRecords_returnsEmptyList(){
        when(helperSkillRepository.findAll()).thenReturn(List.of());
        
        List<HelperSkill> result = helperSkillService.getAllHelpersSkills();

        assertTrue(result.isEmpty());
        verify(helperSkillRepository, times(1)).findAll();
    }

    @Test
    void getHelpersSkillById_found_returnsRecord(){
        when(helperSkillRepository.findById(1)).thenReturn(Optional.of(sample));

        HelperSkill result = helperSkillService.getHelperSkillById(1);

        assertEquals(sample, result);
        verify(helperSkillRepository).findById(1);
    }

    @Test
    void getHelperSkillById_notFound_returnsNull(){
        when(helperSkillRepository.findById(99)).thenReturn(Optional.empty());

        HelperSkill result = helperSkillService.getHelperSkillById(99);
        assertNull(result);
        verify(helperSkillRepository).findById(99);
    }

    @Test
    void saveHelperSkill_validRecord_savesAndReturns(){
        when(helperSkillRepository.save(sample)).thenReturn(sample);

        HelperSkill result = helperSkillService.saveHelperSkill(sample);

        assertEquals(sample, result);
        verify(helperSkillRepository).save(sample);
    }

    @Test
    void saveHelperSkill_null_returnsNUllWithoutCallingRepository(){
        HelperSkill result = helperSkillService.saveHelperSkill(null);

        assertNull(result);
        verify(helperSkillRepository, never()).save(any(HelperSkill.class));
    }

    @Test
    void updateHelperSkill_found_updatesFieldsAndSaves(){
        HelperSkill existing = HelperSkill.builder().helperSkillId(1).build();

        HelperSkill updated = HelperSkill.builder().helperSkillId(1).build();

        when(helperSkillRepository.findById(1)).thenReturn(Optional.of(existing));
        when(helperSkillRepository.save(existing)).thenReturn(existing);

        HelperSkill result = helperSkillService.updateHelperSkill(1, updated);

        assertEquals(existing, result);
        assertEquals(updated.getHelperId(), existing.getHelperId());
        assertEquals(updated.getTaskTypeId(), existing.getTaskTypeId());
        verify(helperSkillRepository).findById(1);
        verify(helperSkillRepository).save(existing);
    }

    @Test
    void updateHelperSkill_notFound_returnsNullWithoutSaveing(){
        when(helperSkillRepository.findById(99)).thenReturn(Optional.empty());

        HelperSkill result = helperSkillService.updateHelperSkill(99, sample);

        assertNull(result);
        verify(helperSkillRepository).findById(99);
        verify(helperSkillRepository, never()).save(any(HelperSkill.class));
    }

    @Test
    void deleteHelperSkill_exists_deletesAndReturnsTrue(){
        when(helperSkillRepository.existsById(1)).thenReturn(true);

        boolean result = helperSkillService.deleteHelperSkill(1);
        assertTrue(result);
        verify(helperSkillRepository).existsById(1);
        verify(helperSkillRepository).deleteById(1);
    }

    @Test
    void deleteHelperSkill_notExists_returnsFalseWithourDeleting(){

        when(helperSkillRepository.existsById(99)).thenReturn(false);

        boolean result = helperSkillService.deleteHelperSkill(99);

        assertFalse(result);
        verify(helperSkillRepository).existsById(99);
        verify(helperSkillRepository, never()).deleteById(anyInt());
    }
}
