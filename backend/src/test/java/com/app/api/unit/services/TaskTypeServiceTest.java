package com.app.api.unit.services;

import com.app.api.models.Badges;
import com.app.api.models.Ratings;
import com.app.api.models.TaskType;
import com.app.api.repositories.TaskTypeRepository;
import com.app.api.services.TaskTypeService;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class TaskTypeServiceTest {

    @Mock
    private TaskTypeRepository taskTypeRepository;

    @InjectMocks
    private TaskTypeService taskTypeService;

    private Badges mockBadges;
    private Ratings mockRatings;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
        
        mockRatings = new Ratings();
        mockRatings.setRatingid(1);
        
        mockBadges = new Badges();
        mockBadges.setBadgeid(1);
        mockBadges.setBadgeName("Test Badge");
        mockBadges.setBadgeDescription("Test Description");
        mockBadges.setIsSpecialist(true);
        mockBadges.setXpReward(100);
        mockBadges.setRatingid(mockRatings);
    }

    @Test
    void getAllTaskTypes_ReturnAllTaskTypes() {
        
        TaskType type1 = new TaskType();
        type1.setTasktypeid(1);
        type1.setDescription("Cleaning");
        type1.setXpWorth(100);
        type1.setNeedsSpecialist(false);
        type1.setBadgeid(mockBadges);

        TaskType type2 = new TaskType();
        type2.setTasktypeid(2);
        type2.setDescription("Plumbing");
        type2.setXpWorth(200);
        type2.setNeedsSpecialist(true);
        type2.setBadgeid(mockBadges);

        List<TaskType> taskTypes = Arrays.asList(type1, type2);
        when(taskTypeRepository.findAll()).thenReturn(taskTypes);

        List<TaskType> result = taskTypeService.getAllTaskTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTasktypeid());
        assertEquals("Cleaning", result.get(0).getDescription());
        assertEquals(100, result.get(0).getXpWorth());
        assertFalse(result.get(0).isNeedsSpecialist());
        assertEquals(mockBadges, result.get(0).getBadgeid());
        verify(taskTypeRepository, times(1)).findAll();
    }

    @Test
    void getAllTaskTypes_NoTaskTypesExist() {
        
        when(taskTypeRepository.findAll()).thenReturn(List.of());

        List<TaskType> result = taskTypeService.getAllTaskTypes();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskTypeRepository, times(1)).findAll();
    }

    @Test
    void getTaskTypeById_ReturnTaskType() {
        
        int id = 1;
        TaskType taskType = new TaskType();
        taskType.setTasktypeid(id);
        taskType.setDescription("Gardening");
        taskType.setXpWorth(150);
        taskType.setNeedsSpecialist(false);
        taskType.setBadgeid(mockBadges);

        when(taskTypeRepository.findById(id)).thenReturn(Optional.of(taskType));

        TaskType result = taskTypeService.getTaskTypeById(id);

        assertNotNull(result);
        assertEquals(id, result.getTasktypeid());
        assertEquals("Gardening", result.getDescription());
        assertEquals(150, result.getXpWorth());
        assertFalse(result.isNeedsSpecialist());
        assertEquals(mockBadges, result.getBadgeid());
        verify(taskTypeRepository, times(1)).findById(id);
    }

    @Test
    void getTaskTypeById_TaskTypeDoesNotExist() {
        
        int id = 999;
        when(taskTypeRepository.findById(id)).thenReturn(Optional.empty());

        TaskType result = taskTypeService.getTaskTypeById(id);

        assertNull(result);
        verify(taskTypeRepository, times(1)).findById(id);
    }

    @Test
    void saveTaskType_SaveAndReturnTaskType() {
        
        TaskType taskType = new TaskType();
        taskType.setTasktypeid(1);
        taskType.setDescription("New Task Type");
        taskType.setXpWorth(100);
        taskType.setNeedsSpecialist(false);
        taskType.setBadgeid(mockBadges);

        when(taskTypeRepository.save(taskType)).thenReturn(taskType);

        TaskType result = taskTypeService.saveTaskType(taskType);

        assertNotNull(result);
        assertEquals(1, result.getTasktypeid());
        assertEquals("New Task Type", result.getDescription());
        assertEquals(100, result.getXpWorth());
        assertFalse(result.isNeedsSpecialist());
        assertEquals(mockBadges, result.getBadgeid());
        verify(taskTypeRepository, times(1)).save(taskType);
    }

    @Test
    void saveTaskType_ReturnNull() {
    
        TaskType result = taskTypeService.saveTaskType(null);

        assertNull(result);
        verify(taskTypeRepository, never()).save(any(TaskType.class));
    }

    @Test
    void saveTaskType_HandleTaskTypeWithAllFields() {
        
        TaskType taskType = new TaskType();
        taskType.setTasktypeid(2);
        taskType.setDescription("Specialist Task");
        taskType.setXpWorth(250);
        taskType.setNeedsSpecialist(true);
        taskType.setBadgeid(mockBadges);

        when(taskTypeRepository.save(taskType)).thenReturn(taskType);

        TaskType result = taskTypeService.saveTaskType(taskType);

        assertNotNull(result);
        assertEquals(2, result.getTasktypeid());
        assertEquals("Specialist Task", result.getDescription());
        assertEquals(250, result.getXpWorth());
        assertTrue(result.isNeedsSpecialist());
        assertEquals(mockBadges, result.getBadgeid());
        verify(taskTypeRepository, times(1)).save(taskType);
    }

    @Test
    void updateTaskType_UpdateAllFields() {
        
        int id = 1;

        TaskType existing = new TaskType();
        existing.setTasktypeid(id);
        existing.setDescription("Old Description");
        existing.setXpWorth(100);
        existing.setNeedsSpecialist(false);
        existing.setBadgeid(mockBadges);

        Badges newBadges = new Badges();
        newBadges.setBadgeid(2);
        newBadges.setBadgeName("New Badge");
        newBadges.setBadgeDescription("New Description");
        newBadges.setIsSpecialist(true);
        newBadges.setXpReward(200);
        newBadges.setRatingid(mockRatings);

        TaskType updates = new TaskType();
        updates.setDescription("Updated Description");
        updates.setXpWorth(300);
        updates.setNeedsSpecialist(true);
        updates.setBadgeid(newBadges);

        when(taskTypeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(taskTypeRepository.save(existing)).thenReturn(existing);

        TaskType result = taskTypeService.updateTaskType(id, updates);

        assertNotNull(result);
        assertEquals("Updated Description", result.getDescription());
        assertEquals(300, result.getXpWorth());
        assertTrue(result.isNeedsSpecialist());
        assertEquals(2, result.getBadgeid().getBadgeid());
        verify(taskTypeRepository, times(1)).save(existing);
    }

    @Test
    void updateTaskType_ReturnNull_TaskTypeDoesNotExist() {
        
        int id = 999;
        TaskType updates = new TaskType();
        updates.setDescription("New Description");

        when(taskTypeRepository.findById(id)).thenReturn(Optional.empty());

        TaskType result = taskTypeService.updateTaskType(id, updates);

        assertNull(result);
        verify(taskTypeRepository, never()).save(any(TaskType.class));
    }

    @Test
    void updateTaskType_ReturnNull_whenUpdatedIsNull() {
        
        int id = 1;

        TaskType result = taskTypeService.updateTaskType(id, null);


        assertNull(result);
        verify(taskTypeRepository, never()).save(any(TaskType.class));
        verify(taskTypeRepository, never()).findById(anyInt());
    }

    @Test
    void updateTaskType_shouldHandlePartialUpdates() {
        
        int id = 1;

        TaskType existing = new TaskType();
        existing.setTasktypeid(id);
        existing.setDescription("Original Description");
        existing.setXpWorth(100);
        existing.setNeedsSpecialist(false);
        existing.setBadgeid(mockBadges);

        
        TaskType updates = new TaskType();
        updates.setDescription("Updated Description");
        updates.setXpWorth(300);
        // Leave needsSpecialist and badgeid null/default

        when(taskTypeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(taskTypeRepository.save(existing)).thenReturn(existing);

        TaskType result = taskTypeService.updateTaskType(id, updates);

        assertNotNull(result);
        assertEquals("Updated Description", result.getDescription());
        assertEquals(300, result.getXpWorth());

        // These should remain unchanged from existing
        assertEquals(mockBadges, result.getBadgeid());
        assertFalse(result.isNeedsSpecialist());
        verify(taskTypeRepository, times(1)).save(existing);
    }

    @Test
    void deleteTaskType_TaskTypeExists() {
       
        int id = 1;
        doNothing().when(taskTypeRepository).deleteById(id);

        taskTypeService.deleteTaskType(id);

        verify(taskTypeRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteTaskType_shouldHandleDelete_whenTaskTypeDoesNotExist() {
        
        int id = 999;
        doNothing().when(taskTypeRepository).deleteById(id);

        taskTypeService.deleteTaskType(id);

        // Should not throw exception even if task type doesn't exist
        verify(taskTypeRepository, times(1)).deleteById(id);
    }

    @Test
    void getTaskTypeById_shouldHandleZeroId() {
    
        int id = 0;
        when(taskTypeRepository.findById(id)).thenReturn(Optional.empty());

        TaskType result = taskTypeService.getTaskTypeById(id);

        assertNull(result);
        verify(taskTypeRepository, times(1)).findById(id);
    }

    @Test
    void saveTaskType_HandleTaskTypeWithNullFields() {
       
        TaskType taskType = new TaskType();
        taskType.setTasktypeid(1);

        when(taskTypeRepository.save(taskType)).thenReturn(taskType);

        TaskType result = taskTypeService.saveTaskType(taskType);

        assertNotNull(result);
        assertEquals(1, result.getTasktypeid());
        assertNull(result.getDescription());
        assertNull(result.getBadgeid());
        assertEquals(0, result.getXpWorth()); 
        assertFalse(result.isNeedsSpecialist()); // boolean default
        verify(taskTypeRepository, times(1)).save(taskType);
    }

    @Test
    void updateTaskType_HandleNullRelationships() {

        int id = 1;

        TaskType existing = new TaskType();
        existing.setTasktypeid(id);
        existing.setDescription("Original");
        existing.setXpWorth(100);
        existing.setNeedsSpecialist(false);
        existing.setBadgeid(mockBadges);

        TaskType updates = new TaskType();
        updates.setBadgeid(null); // Set relationship to null
        updates.setDescription("Updated");

        when(taskTypeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(taskTypeRepository.save(existing)).thenReturn(existing);

        TaskType result = taskTypeService.updateTaskType(id, updates);

        assertNotNull(result);
        assertNull(result.getBadgeid());
        assertEquals("Updated", result.getDescription());
        // These should remain unchanged
        assertEquals(100, result.getXpWorth());
        assertFalse(result.isNeedsSpecialist());
        verify(taskTypeRepository, times(1)).save(existing);
    }

    @Test
    void updateTaskType_HandlePartialUpdateWithOnlyBadge() {
        
        int id = 1;

        TaskType existing = new TaskType();
        existing.setTasktypeid(id);
        existing.setDescription("Original Description");
        existing.setXpWorth(100);
        existing.setNeedsSpecialist(false);
        existing.setBadgeid(mockBadges);

        Badges newBadges = new Badges();
        newBadges.setBadgeid(5);
        newBadges.setBadgeName("New Badge");
        newBadges.setBadgeDescription("New Description");
        newBadges.setIsSpecialist(true);
        newBadges.setXpReward(500);
        newBadges.setRatingid(mockRatings);

        TaskType updates = new TaskType();
        updates.setBadgeid(newBadges); // Only update badge
        // Leave other fields null/default

        when(taskTypeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(taskTypeRepository.save(existing)).thenReturn(existing);

        
        TaskType result = taskTypeService.updateTaskType(id, updates);

        
        assertNotNull(result);
        assertEquals(5, result.getBadgeid().getBadgeid());

        // These should remain unchanged
        assertEquals("Original Description", result.getDescription());
        assertEquals(100, result.getXpWorth());
        assertFalse(result.isNeedsSpecialist());
        verify(taskTypeRepository, times(1)).save(existing);
    }
}

// Missing null check on updated parameter (updateTaskType_ReturnNull_whenUpdatedIsNull())
// badgeid becomes null during partial update (updateTaskType_shouldHandlePartialUpdates())
// description becomes null during partial update (updateTaskType_HandlePartialUpdateWithOnlyBadge())
// xpWorth overwritten with 0 during partial update (updateTaskType_HandleNullRelationships())