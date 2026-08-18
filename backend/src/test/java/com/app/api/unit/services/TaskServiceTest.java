package com.app.api.unit.services;

import java.time.LocalDate;
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

import com.app.api.dtos.TaskDetailDTO;
import com.app.api.events.TaskStartedEvent;
import com.app.api.models.Dependent;
import com.app.api.models.Helper;
import com.app.api.models.Task;
import com.app.api.models.TaskInvitation;
import com.app.api.models.TaskInvoice;
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
    @SuppressWarnings("unused")
    void setUp()
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

    

    @Test
    void fullName_bothFirstAndLastName_returnsFullName() {
        // This is a private method, so we test it indirectly through toDetailDTO
        // We'll test this through getTaskDetailById or toDetailDTO
        int taskId = 1;
        int dependentId = 1;
        
        Task task = new Task();
        task.setTaskId(taskId);
        task.setDependentId(dependentId);
        
        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        dependent.setUserId(user);
        
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(dependentRepo.findById(dependentId)).thenReturn(Optional.of(dependent));
        
        TaskDetailDTO result = taskService.getTaskDetailById(taskId);
        
        assertNotNull(result);
        assertEquals("John Doe", result.getRequesterName());
    }

    @Test
    void fullName_onlyFirstName_returnsFirstName() {
        int taskId = 1;
        int dependentId = 1;
        
        Task task = new Task();
        task.setTaskId(taskId);
        task.setDependentId(dependentId);
        
        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);
        User user = new User();
        user.setFirstName("John");
        user.setLastName(null);
        dependent.setUserId(user);
        
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(dependentRepo.findById(dependentId)).thenReturn(Optional.of(dependent));
        
        TaskDetailDTO result = taskService.getTaskDetailById(taskId);
        
        assertNotNull(result);
        assertEquals("John", result.getRequesterName());
    }

    @Test
    void fullName_onlyLastName_returnsLastName() {
        int taskId = 1;
        int dependentId = 1;
        
        Task task = new Task();
        task.setTaskId(taskId);
        task.setDependentId(dependentId);
        
        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);
        User user = new User();
        user.setFirstName(null);
        user.setLastName("Doe");
        dependent.setUserId(user);
        
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(dependentRepo.findById(dependentId)).thenReturn(Optional.of(dependent));
        
        TaskDetailDTO result = taskService.getTaskDetailById(taskId);
        
        assertNotNull(result);
        assertEquals("Doe", result.getRequesterName());
    }

    @Test
    void fullName_noName_returnsNull() {
        int taskId = 1;
        int dependentId = 1;
        
        Task task = new Task();
        task.setTaskId(taskId);
        task.setDependentId(dependentId);
        
        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);
        User user = new User();
        user.setFirstName(null);
        user.setLastName(null);
        dependent.setUserId(user);
        
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(dependentRepo.findById(dependentId)).thenReturn(Optional.of(dependent));
        
        TaskDetailDTO result = taskService.getTaskDetailById(taskId);
        
        assertNotNull(result);
        assertNull(result.getRequesterName());
    }

    @Test
    void toDetailDTO_taskWithDependentAndHelper_returnsFullDTO() {
        int taskId = 1;
        int dependentId = 1;
        int helperId = 1;
        int userId = 10;
        int helperUserId = 20;
        
        Task task = new Task();
        task.setTaskId(taskId);
        task.setHelperId(helperId);
        task.setDependentId(dependentId);
        task.setImmediate(true);
        task.setLocationId(5);
        task.setTaskTypeId(3);
        task.setNeedsSpecialist(true);
        task.setSignedAdminId(7);
        task.setStartDate(java.sql.Date.valueOf("2026-05-21"));
        task.setEndDate(java.sql.Date.valueOf("2026-05-24"));
        task.setHelperBadgeId(9);
        task.setDependentRatingId("4");
        task.setHelperRatingId("5");
        task.setAdminReview("Good work");
        task.setCompatibilityId(2);
        task.setStatus("open");
        
        // Setup dependent
        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);
        User user = new User();
        user.setUserid(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        dependent.setUserId(user);
        
        // Setup helper
        Helper helper = new Helper();
        helper.setHelperid(helperId);
        User helperUser = new User();
        helperUser.setUserid(helperUserId);
        helperUser.setFirstName("Jane");
        helperUser.setLastName("Smith");
        helper.setUserid(helperUser);
        
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(dependentRepo.findById(dependentId)).thenReturn(Optional.of(dependent));
        when(helperRepo.findById(helperId)).thenReturn(Optional.of(helper));
        
        TaskDetailDTO result = taskService.getTaskDetailById(taskId);
        
        assertNotNull(result);
        assertEquals(taskId, result.getTaskId());
        assertEquals(helperId, result.getHelperId());
        assertEquals(dependentId, result.getDependentId());
        assertTrue(result.isImmediate());
        assertEquals(5, result.getLocationId());
        assertEquals(3, result.getTaskTypeId());
        assertTrue(result.isNeedsSpecialist());
        assertEquals(7, result.getSignedAdminId());
        assertEquals(java.sql.Date.valueOf("2026-05-21"), result.getStartDate());
        assertEquals(java.sql.Date.valueOf("2026-05-24"), result.getEndDate());
        assertEquals(9, result.getHelperBadgeId());
        assertEquals("4", result.getDependentRatingId());
        assertEquals("5", result.getHelperRatingId());
        assertEquals("Good work", result.getAdminReview());
        assertEquals(2, result.getCompatibilityId());
        assertEquals("open", result.getStatus());
        assertEquals("John Doe", result.getRequesterName());
        assertEquals("Jane Smith", result.getHelperName());
    }

    @Test
    void getTaskDetailById_taskFound_returnsTaskDetail() {
        int taskId = 1;
        int dependentId = 1;
        
        Task task = new Task();
        task.setTaskId(taskId);
        task.setDependentId(dependentId);
        
        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        dependent.setUserId(user);
        
        when(taskRepo.findById(taskId)).thenReturn(Optional.of(task));
        when(dependentRepo.findById(dependentId)).thenReturn(Optional.of(dependent));
        
        TaskDetailDTO result = taskService.getTaskDetailById(taskId);
        
        assertNotNull(result);
        assertEquals(taskId, result.getTaskId());
        assertEquals("John Doe", result.getRequesterName());
    }

    @Test
    void getTaskDetailById_taskNotFound_returnsNull() {
        int taskId = 999;
        
        when(taskRepo.findById(taskId)).thenReturn(Optional.empty());
        
        TaskDetailDTO result = taskService.getTaskDetailById(taskId);
        
        assertNull(result);
    }

    @Test
    void getAllTaskDetailsByUserId_helperExistsWithPendingInvitations_returnsTaskDetails() {
        int userId = 10;
        int helperId = 1;
        int taskId = 100;
        
        // Setup helper
        Helper helper = new Helper();
        helper.setHelperid(helperId);
        User helperUser = new User();
        helperUser.setUserid(userId);
        helperUser.setFirstName("Jane");
        helperUser.setLastName("Smith");
        helper.setUserid(helperUser);
        
        // Setup task invoice
        TaskInvoice invoice = new TaskInvoice();
        invoice.setTaskid(taskId);
        
        // Setup dependent for the invoice
        Dependent dependent = new Dependent();
        dependent.setDependentId(2);
        User dependentUser = new User();
        dependentUser.setUserid(20);
        dependentUser.setFirstName("John");
        dependentUser.setLastName("Doe");
        dependent.setUserId(dependentUser);
        
        // Setup task invitation
        TaskInvitation invitation = new TaskInvitation();
        invitation.setTaskId(invoice);
        invitation.setHelperId(helper);
        invitation.setStatus("pending");
        
        List<TaskInvitation> invitations = List.of(invitation);
        
        when(helperRepo.findByUserid_Userid(userId)).thenReturn(Optional.of(helper));
        when(taskInvitationRepo.findByHelperId_HelperidAndStatus(helperId, null)).thenReturn(invitations);
        
        // Mock the dependent repo for the invoice
        when(dependentRepo.findById(2)).thenReturn(Optional.of(dependent));
        
        List<TaskDetailDTO> result = taskService.getAllTaskDetailsByUserId(userId);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(taskId, result.get(0).getTaskId());
    }

    @Test
    void getAllTaskDetailsByUserId_helperExistsWithNoInvitations_returnsEmptyList() {
        int userId = 10;
        int helperId = 1;
        
        Helper helper = new Helper();
        helper.setHelperid(helperId);
        User helperUser = new User();
        helperUser.setUserid(userId);
        helper.setUserid(helperUser);
        
        when(helperRepo.findByUserid_Userid(userId)).thenReturn(Optional.of(helper));
        when(taskInvitationRepo.findByHelperId_HelperidAndStatus(helperId, null)).thenReturn(List.of());
        
        List<TaskDetailDTO> result = taskService.getAllTaskDetailsByUserId(userId);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllTaskDetailsByUserId_helperNotFound_returnsEmptyList() {
        int userId = 999;
        
        when(helperRepo.findByUserid_Userid(userId)).thenReturn(Optional.empty());
        
        List<TaskDetailDTO> result = taskService.getAllTaskDetailsByUserId(userId);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void convertToTask_validInvoice_returnsTask() {
        // This is a private method, tested indirectly through getAllTaskDetailsByUserId
        int taskId = 100;
        int helperId = 1;
        int dependentId = 2;
        
        // Setup helper
        Helper helper = new Helper();
        helper.setHelperid(helperId);
        User helperUser = new User();
        helperUser.setUserid(10);
        helper.setUserid(helperUser);
        
        // Setup dependent
        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);
        User dependentUser = new User();
        dependentUser.setUserid(20);
        dependentUser.setFirstName("John");
        dependentUser.setLastName("Doe");
        dependent.setUserId(dependentUser);
        
        // Setup invoice with all fields
        TaskInvoice invoice = new TaskInvoice();
        invoice.setTaskid(taskId);
        invoice.setHelperid(helper);
        invoice.setDependentid(dependent);
        invoice.setImmediate(true);
        invoice.setNeedsspecialist(false);
        invoice.setStartdate(LocalDate.parse("2026-05-21"));
        invoice.setEnddate(LocalDate.parse("2026-05-24"));
        invoice.setStatus("open");
        
        // Setup invitation
        TaskInvitation invitation = new TaskInvitation();
        invitation.setTaskId(invoice);
        invitation.setHelperId(helper);
        invitation.setStatus("pending");
        
        List<TaskInvitation> invitations = List.of(invitation);
        
        when(helperRepo.findByUserid_Userid(10)).thenReturn(Optional.of(helper));
        when(taskInvitationRepo.findByHelperId_HelperidAndStatus(helperId, null)).thenReturn(invitations);
        when(dependentRepo.findById(dependentId)).thenReturn(Optional.of(dependent));
        
        List<TaskDetailDTO> result = taskService.getAllTaskDetailsByUserId(10);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(taskId, result.get(0).getTaskId());
        assertEquals(helperId, result.get(0).getHelperId());
        assertEquals(dependentId, result.get(0).getDependentId());
        assertEquals("open", result.get(0).getStatus());
    }


    @Test
    void getTaskDetailsByUserId_dependentExists_returnsTaskDetails() {
        int userId = 10;
        int dependentId = 1;
        int taskId = 100;
        
        // Setup dependent
        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);
        User user = new User();
        user.setUserid(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        dependent.setUserId(user);
        
        // Setup task
        Task task = new Task();
        task.setTaskId(taskId);
        task.setDependentId(dependentId);
        
        when(dependentRepo.findByUserId_Userid(userId)).thenReturn(dependent);
        when(taskRepo.findByDependentId(dependentId)).thenReturn(List.of(task));
        when(dependentRepo.findById(dependentId)).thenReturn(Optional.of(dependent));
        
        List<TaskDetailDTO> result = taskService.getTaskDetailsByUserId(userId);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(taskId, result.get(0).getTaskId());
        assertEquals("John Doe", result.get(0).getRequesterName());
    }

    @Test
    void getTaskDetailsByUserId_dependentNotFound_returnsNull() {
        int userId = 999;
        
        when(dependentRepo.findByUserId_Userid(userId)).thenReturn(null);
        
        List<TaskDetailDTO> result = taskService.getTaskDetailsByUserId(userId);
        
        assertNull(result);
    }

    @Test
    void getTaskDetailsByUserId_dependentExistsNoTasks_returnsEmptyList() {
        int userId = 10;
        int dependentId = 1;
        
        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);
        User user = new User();
        user.setUserid(userId);
        dependent.setUserId(user);
        
        when(dependentRepo.findByUserId_Userid(userId)).thenReturn(dependent);
        when(taskRepo.findByDependentId(dependentId)).thenReturn(List.of());
        
        List<TaskDetailDTO> result = taskService.getTaskDetailsByUserId(userId);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
