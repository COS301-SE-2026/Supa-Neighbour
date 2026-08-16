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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.app.api.dtos.TaskDetailDTO;
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

        when(taskRepo.findById(id)).thenReturn(Optional.of(existing));
        when(taskRepo.save(existing)).thenReturn(existing);

        
        Task updatedTask = taskService.updateTask(id, updates);

        
        assertNotNull(updatedTask);
        assertEquals(5, updatedTask.getHelperId());
        assertEquals(3, updatedTask.getDependentId());
        assertEquals(2, updatedTask.getTaskTypeId());
        assertEquals(4, updatedTask.getLocationId());
        assertEquals("All fields updated", updatedTask.getAdminReview());
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
    void getTaskDetailById_found_notHelperOrDependent_returnsBasicDetail(){

        int id = 2001;
        Task task = new Task();
        task.setTaskId(id);
        task.setStatus("open");
        task.setImmediate(true);
        task.setNeedsSpecialist(false);

        when(taskRepo.findById(id)).thenReturn(Optional.of(task));
        
        TaskDetailDTO detail = taskService.getTaskDetailById(id);

        assertNotNull(detail);
        assertEquals(id, detail.getTaskId());
        assertEquals("open", detail.getStatus());
        assertNull(detail.getRequesterName());
        assertNull(detail.getHelperName());
        verify(dependentRepo, never()).findById(anyInt());
        verify(helperRepo, never()).findById(anyInt());
    }

    @Test
    void getTaskDetailById_notFound_returnsNull(){
        when(taskRepo.findById(9999)).thenReturn(Optional.empty());

        TaskDetailDTO detail = taskService.getTaskDetailById(9999);
        assertNull(detail);
        verify(taskRepo, times(1)).findById(9999);
    }

    @Test
    void getTaskDetailaById_found_dependentAndHelperResolved_namesPopulated(){
        int id = 2002;
        int dependentId = 7;
        int helperId = 9;

        Task task = new Task();
        task.setTaskId(id);
        task.setDependentId(dependentId);
        task.setHelperId(helperId);

        User dependentUser = mock(User.class);
        when(dependentUser.getFirstName()).thenReturn("Jane");
        when(dependentUser.getLastName()).thenReturn("Doe");

        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);
        dependent.setUserId(dependentUser);

        User helperUser = mock(User.class);
        when(helperUser.getFirstName()).thenReturn("John");
        when(helperUser.getLastName()).thenReturn("Smith");

        Helper helper = Helper.builder().helperid(helperId).userid(helperUser).build();

        when(taskRepo.findById(id)).thenReturn(Optional.of(task));
        when(dependentRepo.findById(dependentId)).thenReturn(Optional.of(dependent));
        when(helperRepo.findById(helperId)).thenReturn(Optional.of(helper));

        TaskDetailDTO detail = taskService.getTaskDetailById(id);
        assertNotNull(detail);
        assertEquals("Jane Doe", detail.getRequesterName());
        assertEquals("John Smith", detail.getHelperName());
    }

    @Test 
    void getTaskDetailsById_found_dependentUserNull_skipsRequesterName(){
        int id = 2003;
        int dependentId = 8;

        Task task = new Task();
        task.setTaskId(id);
        task.setDependentId(dependentId);

        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);

        when(taskRepo.findById(id)).thenReturn(Optional.of(task));
        TaskDetailDTO detail = taskService.getTaskDetailById(id);

        assertNotNull(detail);
        assertNull(detail.getRequesterName());
    }

    @Test
    void getTaskDetailById_found_helperNotInRepo_skipsHelperName(){
        int id = 2004;
        int helperId = 11;

        Task task = new Task();
        task.setTaskId(id);
        task.setHelperId(helperId);

        when(taskRepo.findById(id)).thenReturn(Optional.of(task));
        when(helperRepo.findById(helperId)).thenReturn(Optional.empty());

        TaskDetailDTO detail = taskService.getTaskDetailById(id);

        assertNotNull(detail);
        assertNull(detail.getHelperName());
    }

    @Test
    void getAllTaskDetailsByUserId_noHelperProfile_returnEmptylist(){
        when(helperRepo.findByUserid_Userid(999)).thenReturn(Optional.empty());

        List<TaskDetailDTO> details = taskService.getAllTaskDetailsByUserId(999);
        assertNotNull(details);
        assertTrue(details.isEmpty());
        verify(taskInvitationRepo, never()).findByHelperId_HelperidAndStatus(anyInt(), org.mockito.ArgumentMatchers.any());
        
    }

    @Test
    void getAllTaskDetailsByUserId_helperExistsButNoPendingInvitations_returnsEmptyList(){
        int userId = 43;
        int helperId = 6;

        Helper helper = Helper.builder().helperid(helperId).build();

        when(helperRepo.findByUserid_Userid(userId)).thenReturn(Optional.of(helper));
        when(taskInvitationRepo.findByHelperId_HelperidAndStatus(helperId, null)).thenReturn(List.of());
        List<TaskDetailDTO> details = taskService.getAllTaskDetailsByUserId(userId);

        assertNotNull(details);
        assertTrue(details.isEmpty());
    }

    @Test
    void getAllTaskDetailsByUserId_withPendingInvitations_returnsDetails() {
        int userId = 42;
        int helperId = 5;

        Helper helper = Helper.builder()
                .helperid(helperId)
                .build();

        TaskInvoice invoice = mock(TaskInvoice.class);
        when(invoice.getTaskid()).thenReturn(3001);
        when(invoice.getHelperid()).thenReturn(null);
        when(invoice.getDependentid()).thenReturn(null);
        when(invoice.getImmediate()).thenReturn(Boolean.TRUE);
        when(invoice.getLocationid()).thenReturn(null);
        when(invoice.getTasktypeid()).thenReturn(null);
        when(invoice.isNeedsspecialist()).thenReturn(false);
        when(invoice.getSignedadminid()).thenReturn(null);
        when(invoice.getStartdate()).thenReturn(null);
        when(invoice.getEnddate()).thenReturn(null);
        when(invoice.getHelperbadgeid()).thenReturn(null);
        when(invoice.getDependentRatingreview()).thenReturn("Good");
        when(invoice.getHelperRatingreview()).thenReturn("Great");
        when(invoice.getAdminReview()).thenReturn(null);
        when(invoice.getStatus()).thenReturn("Invited");
        when(invoice.getCompatibilityid()).thenReturn(null);

        TaskInvitation invitation = TaskInvitation.builder()
                .taskInvitationId(1)
                .taskId(invoice)
                .helperId(helper)
                .status("Invited")
                .build();

        when(helperRepo.findByUserid_Userid(userId)).thenReturn(Optional.of(helper));
        when(taskInvitationRepo.findByHelperId_HelperidAndStatus(helperId, null))
                .thenReturn(List.of(invitation));

        List<TaskDetailDTO> details = taskService.getAllTaskDetailsByUserId(userId);

        assertNotNull(details);
        assertEquals(1, details.size());
        assertEquals(3001, details.get(0).getTaskId());
        assertEquals("Invited", details.get(0).getStatus());
        assertEquals("Good", details.get(0).getDependentRatingId());
        assertEquals("Great", details.get(0).getHelperRatingId());
    }

    @Test
    void getTaskDetailsByUserId_noDependentProfile_returnsNull() {

        int userId = 999;

        when(dependentRepo.findByUserId_Userid(userId))
                .thenReturn(null);

        List<TaskDetailDTO> result =
                taskService.getTaskDetailsByUserId(userId);

        assertNull(result);

        verify(dependentRepo)
                .findByUserId_Userid(userId);

        verify(taskRepo, never())
                .findByDependentId(anyInt());
    }

    @Test
    void getTaskDetailsByUserId_dependentExists_returnrTaskDetails(){
        int userId = 103;
        int dependentId = 7;
        Dependent dependent = new Dependent();
        dependent.setDependentId(dependentId);

        Task task = new Task();

        task.setTaskId(1001);
        task.setStatus("open");

        when(dependentRepo.findByUserId_Userid(userId)).thenReturn(dependent);
        when(taskRepo.findByDependentId(dependentId)).thenReturn(List.of(task));

        List<TaskDetailDTO> result = taskService.getTaskDetailsByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(1001, result.get(0).getTaskId());
        assertEquals("open", result.get(0).getStatus());
        verify(dependentRepo).findByUserId_Userid(userId);
        verify(taskRepo).findByDependentId(dependentId);
    }
}
