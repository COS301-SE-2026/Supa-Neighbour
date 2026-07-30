package com.app.api.unit.services;

import com.app.api.models.Badges;
import com.app.api.models.Helper;
import com.app.api.models.TaskType;
import com.app.api.models.User;
import com.app.api.repositories.HelperRepository;
import com.app.api.services.HelperService;

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

public class HelperServiceTest {

    @Mock
    private HelperRepository helperRepository;

    @InjectMocks
    private HelperService helperService;

    private User mockUser;
    private TaskType mockTaskType;
    private Badges mockBadges;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();

        mockTaskType = new TaskType();
        mockTaskType.setTasktypeid(1);

        mockBadges = new Badges();
        mockBadges.setBadgeid(1);
    }

    // All tests follow the AAA pattern (Arrange, Act, Assert) and are designed to be independent of each other.

    @Test
    void getAllHelpers_ReturnAllHelpers() {

        Helper helper1 = new Helper();
        helper1.setHelperid(1);
        helper1.setHelperXp(50);

        Helper helper2 = new Helper();
        helper2.setHelperid(2);
        helper2.setHelperXp(75);

        List<Helper> helpers = Arrays.asList(helper1, helper2);
        when(helperRepository.findAll()).thenReturn(helpers);

        List<Helper> result = helperService.getAllHelpers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getHelperid());
        verify(helperRepository, times(1)).findAll();
    }

    @Test
    void getAllHelpers_ReturnEmptyList() {

        when(helperRepository.findAll()).thenReturn(List.of());

        List<Helper> result = helperService.getAllHelpers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(helperRepository, times(1)).findAll();
    }

    @Test
    void getHelperById_ReturnHelper() {

        int id = 1;
        Helper helper = new Helper();
        helper.setHelperid(id);
        helper.setHelperXp(60);

        when(helperRepository.findById(id)).thenReturn(Optional.of(helper));

        Helper result = helperService.getHelperById(id);

        assertNotNull(result);
        assertEquals(id, result.getHelperid());
        assertEquals(60, result.getHelperXp());
        verify(helperRepository, times(1)).findById(id);
    }

    @Test
    void getHelperById_ReturnNull() {

        int id = 999;
        when(helperRepository.findById(id)).thenReturn(Optional.empty());

        Helper result = helperService.getHelperById(id);

        assertNull(result);
        verify(helperRepository, times(1)).findById(id);
    }

    @Test
    void saveHelper_SaveAndReturnHelper() {

        Helper helper = new Helper();
        helper.setHelperid(1);
        helper.setUserid(mockUser);
        helper.setTaskTypeid(mockTaskType);
        helper.setBadgeid(mockBadges);
        helper.setHelperXp(50);
        helper.setAvailable(true);

        when(helperRepository.save(helper)).thenReturn(helper);

        Helper result = helperService.saveHelper(helper);

        assertNotNull(result);
        assertEquals(1, result.getHelperid());
        assertEquals(mockUser, result.getUserid());
        assertEquals(mockTaskType, result.getTaskTypeid());
        assertEquals(mockBadges, result.getBadgeid());
        assertEquals(50, result.getHelperXp());
        assertTrue(result.isAvailable());
        verify(helperRepository, times(1)).save(helper);
    }

    @Test
    void saveHelper_HelperIsNull() {

        Helper result = helperService.saveHelper(null);

        assertNull(result);
        verify(helperRepository, never()).save(any(Helper.class));
    }

    @Test
    void updateHelper_HelperExists() {

        int id = 1;

        Helper existing = new Helper();
        existing.setHelperid(id);
        existing.setUserid(mockUser);
        existing.setTaskTypeid(mockTaskType);
        existing.setBadgeid(mockBadges);
        existing.setHelperXp(30);
        existing.setAvailable(false);

        User newUser = new User();

        TaskType newTaskType = new TaskType();
        newTaskType.setTasktypeid(3);

        Badges newBadges = new Badges();
        newBadges.setBadgeid(9);

        Helper updates = new Helper();
        updates.setUserid(newUser);
        updates.setTaskTypeid(newTaskType);
        updates.setBadgeid(newBadges);
        updates.setHelperXp(80);
        updates.setAvailable(true);

        when(helperRepository.findById(id)).thenReturn(Optional.of(existing));
        when(helperRepository.save(existing)).thenReturn(existing);

        Helper result = helperService.updateHelper(id, updates);

        assertNotNull(result);
        assertEquals(newUser, result.getUserid());
        assertEquals(3, result.getTaskTypeid().getTasktypeid());
        assertEquals(9, result.getBadgeid().getBadgeid());
        assertEquals(80, result.getHelperXp());
        assertTrue(result.isAvailable());
        verify(helperRepository, times(1)).save(existing);
    }

    @Test
    void updateHelper_HelperDoesNotExist() {

        int id = 999;
        Helper updates = new Helper();
        updates.setHelperXp(80);

        when(helperRepository.findById(id)).thenReturn(Optional.empty());

        Helper result = helperService.updateHelper(id, updates);

        assertNull(result);
        verify(helperRepository, never()).save(any(Helper.class));
    }

    @Test
    void deleteHelper_DeleteHelper() {

        int id = 1;
        doNothing().when(helperRepository).deleteById(id);

        helperService.deleteHelper(id);

        verify(helperRepository, times(1)).deleteById(id);
    }

    @Test
    void findAllByStatus_ReturnAvailableHelpers() {

        Helper helper1 = new Helper();
        helper1.setHelperid(1);
        helper1.setAvailable(true);

        List<Helper> helpers = Arrays.asList(helper1);
        when(helperRepository.findByAvailable(true)).thenReturn(helpers);

        List<Helper> result = helperService.findAllByStatus(true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isAvailable());
        verify(helperRepository, times(1)).findByAvailable(true);
    }

    @Test
    void findAllByStatus_ReturnUnavailableHelpers() {

        when(helperRepository.findByAvailable(false)).thenReturn(List.of());

        List<Helper> result = helperService.findAllByStatus(false);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(helperRepository, times(1)).findByAvailable(false);
    }
}