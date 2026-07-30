package com.app.api.unit.services;

import com.app.api.models.Dependent;
import com.app.api.models.TaskType;
import com.app.api.models.User;
import com.app.api.repositories.DependentRepository;
import com.app.api.services.DependentService;

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

public class DependentServiceTest {

    @Mock
    private DependentRepository dependentRepository;

    @InjectMocks
    private DependentService dependentService;

    private User mockUser;
    private TaskType mockTaskType;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();

        mockTaskType = new TaskType();
        mockTaskType.setTasktypeid(1);
    }

    // All tests follow the AAA pattern (Arrange, Act, Assert) and are designed to be independent of each other.

    @Test
    void getAllDependents_ReturnAllDependents() {

        Dependent dependent1 = new Dependent();
        dependent1.setDependentId(1);

        Dependent dependent2 = new Dependent();
        dependent2.setDependentId(2);

        List<Dependent> dependents = Arrays.asList(dependent1, dependent2);
        when(dependentRepository.findAll()).thenReturn(dependents);

        List<Dependent> result = dependentService.getAllDependents();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getDependentId());
        verify(dependentRepository, times(1)).findAll();
    }

    @Test
    void getAllDependents_ReturnEmptyList() {

        when(dependentRepository.findAll()).thenReturn(List.of());

        List<Dependent> result = dependentService.getAllDependents();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(dependentRepository, times(1)).findAll();
    }

    @Test
    void getDependentById_ReturnDependent() {

        int id = 1;
        Dependent dependent = new Dependent();
        dependent.setDependentId(id);
        dependent.setUserId(mockUser);

        when(dependentRepository.findById(id)).thenReturn(Optional.of(dependent));

        Dependent result = dependentService.getDependentById(id);

        assertNotNull(result);
        assertEquals(id, result.getDependentId());
        assertEquals(mockUser, result.getUserId());
        verify(dependentRepository, times(1)).findById(id);
    }

    @Test
    void getDependentById_ReturnNull() {

        int id = 999;
        when(dependentRepository.findById(id)).thenReturn(Optional.empty());

        Dependent result = dependentService.getDependentById(id);

        assertNull(result);
        verify(dependentRepository, times(1)).findById(id);
    }

    @Test
    void saveDependent_SaveAndReturnDependent() {

        Dependent dependent = new Dependent();
        dependent.setDependentId(1);
        dependent.setUserId(mockUser);
        dependent.setTaskTypeId(mockTaskType);

        when(dependentRepository.save(dependent)).thenReturn(dependent);

        Dependent result = dependentService.saveDependent(dependent);

        assertNotNull(result);
        assertEquals(1, result.getDependentId());
        assertEquals(mockUser, result.getUserId());
        assertEquals(mockTaskType, result.getTaskTypeId());
        verify(dependentRepository, times(1)).save(dependent);
    }

    @Test
    void saveDependent_DependentIsNull() {

        Dependent result = dependentService.saveDependent(null);

        assertNull(result);
        verify(dependentRepository, never()).save(any(Dependent.class));
    }

    @Test
    void updateDependent_DependentExists() {

        int id = 1;

        Dependent existing = new Dependent();
        existing.setDependentId(id);
        existing.setUserId(mockUser);
        existing.setTaskTypeId(mockTaskType);

        User newUser = new User();

        TaskType newTaskType = new TaskType();
        newTaskType.setTasktypeid(3);

        Dependent updates = new Dependent();
        updates.setUserId(newUser);
        updates.setTaskTypeId(newTaskType);

        when(dependentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(dependentRepository.save(existing)).thenReturn(existing);

        Dependent result = dependentService.updateDependent(id, updates);

        assertNotNull(result);
        assertEquals(newUser, result.getUserId());
        assertEquals(3, result.getTaskTypeId().getTasktypeid());
        verify(dependentRepository, times(1)).save(existing);
    }

    @Test
    void updateDependent_DependentDoesNotExist() {

        int id = 999;
        Dependent updates = new Dependent();
        updates.setUserId(mockUser);

        when(dependentRepository.findById(id)).thenReturn(Optional.empty());

        Dependent result = dependentService.updateDependent(id, updates);

        assertNull(result);
        verify(dependentRepository, never()).save(any(Dependent.class));
    }

    @Test
    void deleteDependent_DeleteDependent() {

        int id = 1;
        doNothing().when(dependentRepository).deleteById(id);

        dependentService.deleteDependent(id);

        verify(dependentRepository, times(1)).deleteById(id);
    }
}