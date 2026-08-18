package com.app.api.unit.services;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import com.app.api.events.TaskStartedEvent;
import com.app.api.models.Dependent;
import com.app.api.models.Helper;
import com.app.api.models.Task;
import com.app.api.models.User;
import com.app.api.repositories.AnalyticsRepository;
import com.app.api.repositories.ChatRepository;
import com.app.api.repositories.DependentRepository;
import com.app.api.repositories.HelperRepository;
import com.app.api.repositories.MessageRepository;
import com.app.api.repositories.TaskInvitationRepository;
import com.app.api.repositories.TaskRepository;
import com.app.api.services.TaskService;

public class TaskServiceTest
{
    @Mock
    private TaskRepository taskRepo;

    @Mock
    private AnalyticsRepository analyticsRepo;

    @Mock
    private DependentRepository dependentRepo;

    @Mock
    private ChatRepository chatRepo;

    @Mock
    private MessageRepository messageRepo;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private HelperRepository helperRepo;

    @Mock
    private TaskInvitationRepository taskInvitationRepo;

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

        List<Task> allTasks = taskService.getAllTasks();

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
        when(chatRepo.findByTask_Taskid(id)).thenReturn(List.of());
        when(analyticsRepo.findByTaskid_Taskid(id)).thenReturn(List.of());

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
        updates.setStatus("in_progress");

        when(taskRepo.findById(id)).thenReturn(Optional.of(existing));
        when(taskRepo.save(existing)).thenReturn(existing);

        Dependent dependent = new Dependent();
        dependent.setDependentId(3);
        User user = new User();
        user.setUserid(10);
        dependent.setUserId(user);
        
        Helper helper = new Helper();
        helper.setHelperid(5);
        User helperUser = new User();
        helperUser.setFirstName("John");
        helperUser.setLastName("Doe");
        helper.setUserid(helperUser);

        when(dependentRepo.findById(3)).thenReturn(Optional.of(dependent));
        when(helperRepo.findById(5)).thenReturn(Optional.of(helper));
        
        Task updatedTask = taskService.updateTask(id, updates);

        
        assertNotNull(updatedTask);
        assertEquals(5, updatedTask.getHelperId());
        assertEquals(3, updatedTask.getDependentId());
        assertEquals(2, updatedTask.getTaskTypeId());
        assertEquals(4, updatedTask.getLocationId());
        assertEquals("All fields updated", updatedTask.getAdminReview());
        verify(taskRepo, times(1)).save(existing);
        
        verify(eventPublisher, times(1)).publishEvent(any(TaskStartedEvent.class));
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
        verify(taskRepo, never()).save(any(Task.class));
    }



    @Test
    void getTasksByUserId_success()
    {
        int userId = 103;

        Dependent dependent = new com.app.api.models.Dependent();
        dependent.setDependentId(1);
        dependent.setUserId(new com.app.api.models.User());   

        Task task = new Task();
        task.setTaskId(1001);
        task.setDependentId(1);

        when(dependentRepo.findByUserId_Userid(userId)).thenReturn(dependent);
        when(taskRepo.findByDependentId(1)).thenReturn(List.of(task));
        
        List<Task> userTasks = taskService.getTasksByUserId(userId);
 
        assertNotNull(userTasks);
        verify(dependentRepo, times(1)).findByUserId_Userid(userId);
        verify(taskRepo, times(1)).findByDependentId(1);
    }


    @Test
    void getTasksByUserId_noDependentProfile()
    {
        when(dependentRepo.findByUserId_Userid(999)).thenReturn(null);
    
        List<Task> userTasks = taskService.getTasksByUserId(999);
    
        assertNull(userTasks);
        verify(taskRepo, never()).findByDependentId(anyInt());
    }


    @Test
    void deleteTask_deletesChatsAndMessages()
    {
        int id = 1005;
        int chatId = 50;

        com.app.api.models.Chat chat = new com.app.api.models.Chat();
        chat.setChatId(chatId);

        when(taskRepo.existsById(id)).thenReturn(true);
        when(chatRepo.findByTask_Taskid(id)).thenReturn(List.of(chat));
        when(analyticsRepo.findByTaskid_Taskid(id)).thenReturn(List.of());

        boolean deletedTask = taskService.deleteTask(id);

        assertTrue(deletedTask);
        verify(messageRepo, times(1)).deleteByChatId(chatId);
        verify(chatRepo, times(1)).deleteAll(List.of(chat));
        verify(taskRepo, times(1)).deleteById(id);
    }

    @Test
    void updateTask_statusChangeToInProgress_publishesEvent() {
        int taskId = 1001;
        int helperId = 5;
        int dependentId = 3;
        int requesterUserId = 10;
        String helperName = "John Doe";
        
        // Setup existing task
        Task existing = new Task();
        existing.setTaskId(taskId);
        existing.setHelperId(helperId);
        existing.setDependentId(dependentId);
        existing.setStatus("open");
        
        Task updates = new Task();
        updates.setStatus("in_progress");
        
        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);
        User user = new User();
        user.setUserid(requesterUserId);
        dependent.setUserId(user);
        
        Helper helper = new Helper();
        helper.setHelperid(helperId);
        User helperUser = new User();
        helperUser.setFirstName("John");
        helperUser.setLastName("Doe");
        helper.setUserid(helperUser);
        
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(existing));
        when(dependentRepo.findById(dependentId)).thenReturn(Optional.of(dependent));
        when(helperRepo.findById(helperId)).thenReturn(Optional.of(helper));
        when(taskRepo.save(existing)).thenReturn(existing);
        
        Task updatedTask = taskService.updateTask(taskId, updates);
        
        assertNotNull(updatedTask);
        assertEquals("in_progress", updatedTask.getStatus());
        
        verify(eventPublisher, times(1)).publishEvent(any(TaskStartedEvent.class));
    }

    @Test
    void updateTask_statusChangeToOtherStatus_doesNotPublishEvent() {
        int taskId = 1001;
        
        // Setup existing task
        Task existing = new Task();
        existing.setTaskId(taskId);
        existing.setStatus("open");
        
        // Setup updates - status changes to "completed" not "in_progress"
        Task updates = new Task();
        updates.setStatus("completed");
        
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(existing));
        when(taskRepo.save(existing)).thenReturn(existing);
        
        Task updatedTask = taskService.updateTask(taskId, updates);
        
        assertNotNull(updatedTask);
        assertEquals("completed", updatedTask.getStatus());
        
        // Verify event was NOT published for non "in_progress" status
        verify(eventPublisher, never()).publishEvent(any(TaskStartedEvent.class));
    }

    

}
