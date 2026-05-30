package com.app.api.controllers;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Analytics;
import com.app.api.services.AnalyticsService;
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Get all admins.
     * @return admins
     */
    @GetMapping
    public List<Analytics> getAllAdmins() {
        return analyticsService.getAllAdmins();
    }

    /**
     * Get admin by id.
     * @param id admin id
     * @return admin
     */
    @GetMapping("api/admins/{id}")
    public Analytics getAdminById(@PathVariable int id) {
        return analyticsService.getAdminById(id);
    }

    /**
     * Create admin.
     * @param admin admin
     * @return saved admin  
     */
    @PostMapping("api/admins")
    public Analytics createAdmin(@RequestBody Analytics admin) {
        return analyticsService.saveAdmin(admin);
    }
}
