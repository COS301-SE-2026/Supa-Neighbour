package com.app.api.unit.services;

import com.app.api.models.Task;
import com.app.api.repositories.AnalyticsRepository;
import com.app.api.repositories.TaskRepository;
import com.app.api.repositories.DependentRepository;
import com.app.api.services.TaskService;
import com.app.api.models.Dependent;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;


import java.util.Optional;
import java.util.List;
import java.sql.Date;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest
{
    @Mock
    private TaskRepository taskRepo;

    @Mock
    private AnalyticsRepository analyticsRepo;

    @Mock
    private DependentRepository dependentRepo;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void initMocks()
    {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getTaskById_returnTask()
    {
        // AAA

        int id = 1007;
        Task task = new Task();
        task.setTaskId(id);
        when(taskRepo.findById(id)).thenReturn(Optional.of(task));

        Task taskFound = taskService.getTaskById(id);

        assertNotNull(taskFound);
        assertEquals(id, taskFound.getTaskId());
        verify(taskRepo, times(1)).findById(id);
    }

    @Test
    void getTaskById_taskDNE()
    {
        int id= 1;
        when(taskRepo.findById(id)).thenReturn(Optional.empty());

        Task dne = taskService.getTaskById(id);

        assertNull(dne);
        verify(taskRepo, times(1)).findById(id);
    }

    @Test
    void getAllTasks_allTasksreturned()
    {
        Task task1 = new Task();
        task1.setTaskId(1001);

        Task task2 =new Task();
        task2.setTaskId(1002);
        
        List<Task> tasks = List.of(task1, task2);
        when(taskRepo.findAll()).thenReturn(tasks);

        Iterable<Task> allTasks = taskService.getAllTasks();

        assertNotNull(allTasks);
        verify(taskRepo, times(1)).findAll();
    }


    @Test
    void deleteTask_failCase()
    {
        when(taskRepo.existsById(1)).thenReturn(false);

        boolean dne = taskService.deleteTask(1);

        assertFalse(dne);
        verify(taskRepo, never()).deleteById(1);
    }


    @Test
    void deleteTask_success()
    {
        int id = 1005;
        when(taskRepo.existsById(id)).thenReturn(true);
        when(analyticsRepo.findByTaskId(id)).thenReturn(List.of());

        boolean deletedTask = taskService.deleteTask(id);

        assertTrue(deletedTask);
        verify(taskRepo, times(1)).deleteById(id);
    }


    @Test
    void createTask_success()
    {
        Task task = new Task();
        task.setTaskId(1016);
        when(taskRepo.save(task)).thenReturn(task);

        Task newTask = taskService.createTask(task);

        assertNotNull(newTask);
        assertEquals(1016, newTask.getTaskId());
        verify(taskRepo, times(1)).save(task);
    }

   @Test
   void updateTask_allFieldsUpdated()
   {
        
        int id = 1001;
        Task existing = new Task();
        existing.setTaskId(id);

        Task updates = new Task();
        updates.setHelperId(5);
        updates.setDependentId(3);
        updates.setTaskTypeId(2);
        updates.setLocationId(4);
        updates.setStartDate(java.sql.Date.valueOf("2026-05-21"));
        updates.setEndDate(java.sql.Date.valueOf("2026-05-24"));
        updates.setAdminReview("All fields updated");

        when(taskRepo.findById(id)).thenReturn(Optional.of(existing));
        when(taskRepo.save(existing)).thenReturn(existing);

        
        Task updated_task = taskService.updateTask(id, updates);

        
        assertNotNull(updated_task);
        assertEquals(5, updated_task.getHelperId());
        assertEquals(3, updated_task.getDependentId());
        assertEquals(2, updated_task.getTaskTypeId());
        assertEquals(4, updated_task.getLocationId());
        assertEquals("All fields updated", updated_task.getAdminReview());
        verify(taskRepo, times(1)).save(existing);
}

    @Test
    void updateTask_noFieldsUpdated()
    {
        
        int id = 1001;
        Task existing = new Task();
        existing.setTaskId(id);
        existing.setAdminReview("Original review");

        Task updates = new Task(); // all fields null

        when(taskRepo.findById(id)).thenReturn(Optional.of(existing));
        when(taskRepo.save(existing)).thenReturn(existing);

        
        Task updatedTask = taskService.updateTask(id, updates);

        
        assertNotNull(updatedTask);
        assertEquals("Original review", updatedTask.getAdminReview());
        verify(taskRepo, times(1)).save(existing);
    }


    @Test
    void updateTask_taskNotFound()
    {
        
        when(taskRepo.findById(999)).thenReturn(Optional.empty());

        
        Task dne = taskService.updateTask(999, new Task());

        
        assertNull(dne);
        verify(taskRepo, never()).save(any());
    }



    @Test
    void getTasksByUserId_success()
    {
        int userId = 103;

        Dependent dependent = new com.app.api.models.Dependent();
        dependent.setDependentId(1);
        dependent.setUserId(userId);      

        Task task = new Task();
        task.setTaskId(1001);
        task.setDependentId(1);

        when(dependentRepo.findByUserId(userId)).thenReturn(dependent);
        when(taskRepo.findByDependentId(1)).thenReturn(List.of(task));
        
        Iterable<Task> userTasks = taskService.getTasksByUserId(userId);
 
        assertNotNull(userTasks);
        verify(dependentRepo, times(1)).findByUserId(userId);
        verify(taskRepo, times(1)).findByDependentId(1);
    }


    @Test
    void getTasksByUserId_noDependentProfile()
    {
        when(dependentRepo.findByUserId(999)).thenReturn(null);
    
        Iterable<Task> userTasks = taskService.getTasksByUserId(999);
    
        assertNull(userTasks);
        verify(taskRepo, never()).findByDependentId(anyInt());
    }
}