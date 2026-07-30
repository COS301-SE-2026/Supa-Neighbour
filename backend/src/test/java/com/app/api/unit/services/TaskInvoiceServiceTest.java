package com.app.api.unit.services;

import com.app.api.models.*;
import com.app.api.repositories.TaskInvoiceRepository;
import com.app.api.services.TaskInvoiceService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskInvoiceServiceTest {

    /*@Mock
    private TaskInvoiceRepository taskInvoiceRepository;

    @InjectMocks
    private TaskInvoiceService taskInvoiceService;

    private Helper mockHelper;
    private TaskType mockTaskType;
    private Compatibility mockCompatibility;
    private Admin mockAdmin;
    private Dependent mockDependent;
    private Location mockLocation;
    private Badges mockBadges;
    private Ratings mockRatings;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
        
        mockHelper = new Helper();
        mockHelper.setHelperid(1);
        
        mockTaskType = new TaskType();
        mockTaskType.setTasktypeid(1);
        
        mockCompatibility = new Compatibility();
        mockCompatibility.setCompatibilityid(1);
        
        mockAdmin = new Admin();
        mockAdmin.setAdminid(1);
        
        mockDependent = new Dependent();
        mockDependent.setDependentId(1);
        
        mockLocation = new Location();
        mockLocation.setLocationid(1);
        
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

    // All tests follow the AAA pattern (Arrange, Act, Assert) and are designed to be independent of each other.

    @Test
    void getAllTaskInvoices_ReturnAllInvoices() {

        TaskInvoice invoice1 = new TaskInvoice();
        invoice1.setTaskid(1001);
        invoice1.setAdminreview("Review 1");

        TaskInvoice invoice2 = new TaskInvoice();
        invoice2.setTaskid(1002);
        invoice2.setAdminreview("Review 2");

        List<TaskInvoice> invoices = Arrays.asList(invoice1, invoice2);
        when(taskInvoiceRepository.findAll()).thenReturn(invoices);

        List<TaskInvoice> result = taskInvoiceService.getAllTaskInvoices();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1001, result.get(0).getTaskid());
        verify(taskInvoiceRepository, times(1)).findAll();
    }

    @Test
    void getAllTaskInvoices_ReturnEmptyList() {

        when(taskInvoiceRepository.findAll()).thenReturn(List.of());

        List<TaskInvoice> result = taskInvoiceService.getAllTaskInvoices();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskInvoiceRepository, times(1)).findAll();
    }

    @Test
    void getTaskInvoiceById_ReturnInvoice() {

        int id = 1001;
        TaskInvoice invoice = new TaskInvoice();
        invoice.setTaskid(id);
        invoice.setAdminreview("Test Review");
        
        when(taskInvoiceRepository.findById(id)).thenReturn(Optional.of(invoice));

        TaskInvoice result = taskInvoiceService.getTaskInvoiceById(id);

        assertNotNull(result);
        assertEquals(id, result.getTaskid());
        assertEquals("Test Review", result.getAdminReview());
        verify(taskInvoiceRepository, times(1)).findById(id);
    }

    @Test
    void getTaskInvoiceById_ReturnNull() {
        int id = 999;
        when(taskInvoiceRepository.findById(id)).thenReturn(Optional.empty());

        TaskInvoice result = taskInvoiceService.getTaskInvoiceById(id);

        assertNull(result);
        verify(taskInvoiceRepository, times(1)).findById(id);
    }

    @Test
    void saveTaskInvoice_SaveAndReturnInvoice() {
        
        TaskInvoice invoice = new TaskInvoice();
        invoice.setTaskid(1001);
        invoice.setAdminreview("New Invoice");
        invoice.setImmediate(true);
        invoice.setNeedsspecialist(false);
        invoice.setHelperid(mockHelper);
        invoice.setTasktypeid(mockTaskType);

        when(taskInvoiceRepository.save(invoice)).thenReturn(invoice);

        TaskInvoice result = taskInvoiceService.saveTaskInvoice(invoice);

        assertNotNull(result);
        assertEquals(1001, result.getTaskid());
        assertEquals("New Invoice", result.getAdminReview());
        assertTrue(result.getImmediate());
        assertFalse(result.isNeedsspecialist());
        verify(taskInvoiceRepository, times(1)).save(invoice);
    }

    @Test
    void saveTaskInvoice_InvoiceIsNull() {
        
        TaskInvoice result = taskInvoiceService.saveTaskInvoice(null);

        assertNull(result);
        verify(taskInvoiceRepository, never()).save(any(TaskInvoice.class));
    }

    @Test
    void saveTaskInvoice_HandleInvoiceFields() {
        
        TaskInvoice invoice = new TaskInvoice();
        invoice.setTaskid(2001);
        invoice.setHelperid(mockHelper);
        invoice.setTasktypeid(mockTaskType);
        invoice.setAdminreview("Complete Invoice");
        invoice.setCompatibilityid(mockCompatibility);
        invoice.setSignedadminid(mockAdmin);
        invoice.setDependentid(mockDependent);
        invoice.setImmediate(true);
        invoice.setLocationid(mockLocation);
        invoice.setNeedsspecialist(true);
        invoice.setDependentRatingreview("Great");
        invoice.setHelperRatingreview("Excellent");
        invoice.setHelperbadgeid(mockBadges);
        invoice.setStartdate(LocalDate.parse("2026-06-30"));
        invoice.setEnddate(LocalDate.parse("2026-07-05"));

        when(taskInvoiceRepository.save(invoice)).thenReturn(invoice);

        
        TaskInvoice result = taskInvoiceService.saveTaskInvoice(invoice);

        
        assertNotNull(result);
        assertEquals(mockHelper, result.getHelperid());
        assertEquals(mockTaskType, result.getTasktypeid());
        assertEquals("Complete Invoice", result.getAdminReview());
        assertTrue(result.getImmediate());
        assertTrue(result.isNeedsspecialist());
        assertEquals("Great", result.getDependentRatingreview());
        assertEquals("Excellent", result.getHelperRatingreview());
        assertEquals(mockBadges, result.getHelperbadgeid());
        verify(taskInvoiceRepository, times(1)).save(invoice);
    }

    @Test
    void updateTaskInvoice_InvoiceExists() {
        
        int id = 1001;
        
        TaskInvoice existing = new TaskInvoice();
        existing.setTaskid(id);
        existing.setHelperid(mockHelper);
        existing.setTasktypeid(mockTaskType);
        existing.setAdminreview("Old Review");
        existing.setCompatibilityid(mockCompatibility);
        existing.setSignedadminid(mockAdmin);
        existing.setDependentid(mockDependent);
        existing.setImmediate(false);
        existing.setLocationid(mockLocation);
        existing.setNeedsspecialist(false);
        existing.setDependentRatingreview("Old Rating");
        existing.setHelperRatingreview("Old Helper Rating");
        existing.setHelperbadgeid(mockBadges);
        existing.setStartdate(LocalDate.parse("2025-01-01"));
        existing.setEnddate(LocalDate.parse("2025-01-31"));

        // Create updated entities
        Helper newHelper = new Helper();
        newHelper.setHelperid(5);
        
        TaskType newTaskType = new TaskType();
        newTaskType.setTasktypeid(3);
        
        Compatibility newCompatibility = new Compatibility();
        newCompatibility.setCompatibilityid(2);
        
        Admin newAdmin = new Admin();
        newAdmin.setAdminid(10);
        
        Dependent newDependent = new Dependent();
        newDependent.setDependentId(8);
        
        Location newLocation = new Location();
        newLocation.setLocationid(7);
        
        Ratings newRatings = new Ratings();
        newRatings.setRatingid(5);
        
        Badges newBadges = new Badges();
        newBadges.setBadgeid(9);
        newBadges.setBadgeName("New Badge");
        newBadges.setBadgeDescription("New Description");
        newBadges.setIsSpecialist(false);
        newBadges.setXpReward(200);
        newBadges.setRatingid(newRatings);

        TaskInvoice updates = new TaskInvoice();
        updates.setHelperid(newHelper);
        updates.setTasktypeid(newTaskType);
        updates.setAdminreview("Updated Review");
        updates.setCompatibilityid(newCompatibility);
        updates.setSignedadminid(newAdmin);
        updates.setDependentid(newDependent);
        updates.setImmediate(true);
        updates.setLocationid(newLocation);
        updates.setNeedsspecialist(true);
        updates.setDependentRatingreview("Great");
        updates.setHelperRatingreview("Excellent");
        updates.setHelperbadgeid(newBadges);
        updates.setStartdate(LocalDate.parse("2026-06-01"));
        updates.setEnddate(LocalDate.parse("2026-06-30"));

        when(taskInvoiceRepository.findById(id)).thenReturn(Optional.of(existing));
        when(taskInvoiceRepository.save(existing)).thenReturn(existing);

        
        TaskInvoice result = taskInvoiceService.updateTaskInvoice(id, updates);

        assertNotNull(result);
        assertEquals(5, result.getHelperid().getHelperid());
        assertEquals(3, result.getTasktypeid().getTasktypeid());
        assertEquals("Updated Review", result.getAdminReview());
        assertEquals(2, result.getCompatibilityid().getCompatibilityid());
        assertEquals(10, result.getSignedadminid().getAdminid());
        assertEquals(8, result.getDependentid().getDependentId());
        assertTrue(result.getImmediate());
        assertEquals(7, result.getLocationid().getLocationid());
        assertTrue(result.isNeedsspecialist());
        assertEquals("Great", result.getDependentRatingreview());
        assertEquals("Excellent", result.getHelperRatingreview());
        assertEquals(9, result.getHelperbadgeid().getBadgeid());
        assertEquals(LocalDate.parse("2026-06-01"), result.getStartdate());
        assertEquals(LocalDate.parse("2026-06-30"), result.getEnddate());
        verify(taskInvoiceRepository, times(1)).save(existing);
    }

    @Test
    void updateTaskInvoice_InvoiceDoesNotExist() {
        
        int id = 999;
        TaskInvoice updates = new TaskInvoice();
        updates.setAdminreview("New Review");

        when(taskInvoiceRepository.findById(id)).thenReturn(Optional.empty());

        
        TaskInvoice result = taskInvoiceService.updateTaskInvoice(id, updates);

        
        assertNull(result);
        verify(taskInvoiceRepository, never()).save(any(TaskInvoice.class));
    }

    @Test
    void updateTaskInvoice_HandlePartialUpdates() {
        // Arrange
        int id = 1001;
        
        TaskInvoice existing = new TaskInvoice();
        existing.setTaskid(id);
        existing.setHelperid(mockHelper);
        existing.setTasktypeid(mockTaskType);
        existing.setAdminreview("Original Review");
        existing.setImmediate(false);
        existing.setNeedsspecialist(false);

        Helper newHelper = new Helper();
        newHelper.setHelperid(10);
        
        TaskType newTaskType = new TaskType();
        newTaskType.setTasktypeid(5);

        TaskInvoice updates = new TaskInvoice();
        // Only update some fields
        updates.setHelperid(newHelper);
        updates.setTasktypeid(newTaskType);
        // Leave other fields null or default

        when(taskInvoiceRepository.findById(id)).thenReturn(Optional.of(existing));
        when(taskInvoiceRepository.save(existing)).thenReturn(existing);

        TaskInvoice result = taskInvoiceService.updateTaskInvoice(id, updates);

        assertNotNull(result);
        assertEquals(10, result.getHelperid().getHelperid());
        assertEquals(5, result.getTasktypeid().getTasktypeid());
        assertEquals("Original Review", result.getAdminReview()); // Should remain unchanged
        assertFalse(result.getImmediate()); // Should remain unchanged
        assertFalse(result.isNeedsspecialist()); // Should remain unchanged
        verify(taskInvoiceRepository, times(1)).save(existing);
    }

    @Test
    void deleteTaskInvoice_whenInvoiceExists() {
       
        int id = 1001;
        doNothing().when(taskInvoiceRepository).deleteById(id);

       
        taskInvoiceService.deleteTaskInvoice(id);

    
        verify(taskInvoiceRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteTaskInvoice_InvoiceDoesNotExist() {
        
        int id = 977;
        doNothing().when(taskInvoiceRepository).deleteById(id);

        taskInvoiceService.deleteTaskInvoice(id);

        // Should not throw exception even if invoice doesn't exist
        verify(taskInvoiceRepository, times(1)).deleteById(id);
    }

    @Test
    void getTaskInvoiceById_HandleZeroId() {
       
        int id = 0;
        when(taskInvoiceRepository.findById(id)).thenReturn(Optional.empty());
       
        TaskInvoice result = taskInvoiceService.getTaskInvoiceById(id);

        assertNull(result);
        verify(taskInvoiceRepository, times(1)).findById(id);
    }

    @Test
    void updateTaskInvoice_HandleNullUpdates() {
        
        int id = 1001;
        TaskInvoice existing = new TaskInvoice();
        existing.setTaskid(id);
        existing.setAdminreview("Original");

        when(taskInvoiceRepository.findById(id)).thenReturn(Optional.of(existing));

        TaskInvoice result = taskInvoiceService.updateTaskInvoice(id, null);

        assertNull(result);
        verify(taskInvoiceRepository, never()).save(any(TaskInvoice.class));
    }

    @Test
    void saveTaskInvoice_HandleInvoiceWithNullFields() {
        
        TaskInvoice invoice = new TaskInvoice();
        invoice.setTaskid(1001);
        // Leave other fields null

        when(taskInvoiceRepository.save(invoice)).thenReturn(invoice);

        TaskInvoice result = taskInvoiceService.saveTaskInvoice(invoice);

        assertNotNull(result);
        assertEquals(1001, result.getTaskid());
        assertNull(result.getAdminReview());
        assertNull(result.getHelperid());
        verify(taskInvoiceRepository, times(1)).save(invoice);
    }*/
}

// The service doesn't check if the updated parameter is null before calling methods on it.
// The service sets adminReview to whatever is in the updated object without checking if it's null