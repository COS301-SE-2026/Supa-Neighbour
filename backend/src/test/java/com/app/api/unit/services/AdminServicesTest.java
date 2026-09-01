package com.app.api.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.app.api.models.Admin;
import com.app.api.repositories.AdminRepository;
import com.app.api.services.AdminService;
import com.app.api.models.User;
import com.app.api.models.Address;
import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminServicesTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminServices;

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = new Admin();

        admin.setAdminid(1);
        admin.setUserid(new User());
        admin.setAdminaccesslevel(1);
        admin.setAdmincreatedate(new Date(System.currentTimeMillis()));
    }

    @Test
    void getAllAdmins() {
        when(adminRepository.findAll()).thenReturn(List.of(admin));
        List<Admin> result = adminServices.getAllAdmins();

        assertEquals(1, result.size());
        assertEquals(admin, result.get(0));

        verify(adminRepository).findAll();
    }

    @Test
    void getAdminById_returnsAdmin() {
        when(adminRepository.findById(1)).thenReturn(Optional.of(admin));

        Admin results = adminServices.getAdminById(1);

        assertNotNull(results);
        assertEquals(admin, results);

        verify(adminRepository).findById(1);
    }

    @Test
    void saveAdmin_whenAdminIsValid() {
        when(adminRepository.save(admin)).thenReturn(admin);
        Admin results = adminServices.saveAdmin(admin);

        assertEquals(admin, results);

        verify(adminRepository).save(admin);
    }

    @Test
    void updateAdmin_whenAdminIsValid() {
        Admin updated = new Admin();
        updated.setUserid(new User());
        updated.setAdminaccesslevel(2);
        updated.setAdmincreatedate(new Date(System.currentTimeMillis()));

        when(adminRepository.findById(1)).thenReturn(Optional.of(admin));
        when(adminRepository.save(any(Admin.class))).thenAnswer(i -> i.getArgument(0));

        Admin result = adminServices.updateAdmin(1, updated);

        assertNotNull(result);
        assertEquals(2, result.getAdminaccesslevel());
        assertEquals(updated.getUserid(), result.getUserid());

        verify(adminRepository).findById(1);
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void deleteAdmin_WhenAdminIsValid() {
        doNothing().when(adminRepository).deleteById(1);
        adminServices.deleteAdmin(1);
        verify(adminRepository).deleteById(1);
    }
}
