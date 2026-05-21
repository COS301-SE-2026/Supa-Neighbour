package com.app.api.unit.services;

import com.app.api.models.Task;
import com.app.api.repositories.AnalyticsRepository;
import com.app.api.repositories.TaskRepository;
import com.app.api.repositories.DependentRepository;
import com.app.api.services.TaskService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;

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
        int id = 1006;
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
}