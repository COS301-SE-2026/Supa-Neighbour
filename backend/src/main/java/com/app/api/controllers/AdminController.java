package com.app.api.controllers;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Admin;
import com.app.api.services.AdminService;
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Get all admins.
     * @return admins
     */
    @GetMapping
    public Iterable<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    /**
     * Get admin by id.
     * @param id admin id
     * @return admin
     */
    @GetMapping("api/admins/{id}")
    public Admin getAdminById(@PathVariable int id) {
        return adminService.getAdminById(id);
    }

    /**
     * Create admin.
     * @param admin admin
     * @return saved admin  
     */
    @PostMapping("api/admins")
    public Admin createAdmin(@RequestBody Admin admin) {
        return adminService.saveAdmin(admin);
    }
}
