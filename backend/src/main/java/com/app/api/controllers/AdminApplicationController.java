package com.app.api.controllers;

import com.app.api.dto.AdminApplicationRequest;
import com.app.api.dto.RejectApplicationRequest;
import com.app.api.models.AdminApplication;
import com.app.api.service.AdminApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;

/**
 * REST enpoints for subbmitting and managing admin applications.
 */
@RestController
@RequestMapping("/api/admin/applications")
public class AdminApplicationController {


    private final AdminApplicationService adminApplicationService;
    /**
     * Default constructor for admin application controller.
     */
    public AdminApplicationController() {

    }

    public AdminApplicationController(AdminApplicationService adminApplicationService) {
        this.adminApplicationService = adminApplicationService;
    }

    /**
     * Submits a new admin application.
     */
    @PostMapping
    public ResponseEntity<AdminApplication> createApplication(@RequestBody AdminApplicationRequest request) {
        AdminApplication created = adminApplicationService.createApplication(
            request.gtUserId(), request.getJustification());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves a single application by ID.
     */
    @GetMapping("/{applicationId}")
    public ResponseEntity<AdminApplication> getApplication(@PathVariable Integer applicationId){
        return ResponseEntity.ok(adminApplicationService.getApplicationById(applicationId));
    }

    /**
     * Retrieves all applications submitted, optionally filtered by status or user.
    * Examples:
     *   GET /api/admin-applications
     *   GET /api/admin-applications?status=Pending
     *   GET /api/admin-applications?userId=42
     */ 

    @GetMapping
    public ResponseEntity<List<AdminApplication>> getApplications(@RequestParam(required = false) String status, @RequestParam(required = false) Integer userId) {
        if(status !=null) {
            return ResponseEntity.ok(adminApplicationService.getApplicationsByStatus(status));
        }

        if(userId != null) {
            return ResponseEntity.ok(adminApplicationService.getApplicationsByUserId(userId));
        }
        return ResponseEntity.ok(adminApplicationService.getAllApplications());
    }

    /**
     * Approves an application
     * PUT /api/admin-applications/5/approve?reviewedByUserId=1
     */

    @PutMapping("/{applicationId}/approve")
    public ResponseEntity<AdminAppliation> approveApplication(@PathVariable Integer applicationId,@RequestParam Integer reviewedByUserId) 
    {
        return ResponseEntity.ok(adminApplicationService.approveApplication(applicationId, reviewedByUserId));
    }

    /**
     * Rejects a pending application with a reason
     * PUT /api/admin-applications/5/reject?reviewedUserId=1
     */
    @PutMapping("/{applicationId}/reject")
    public ResponseEntity<AdminApplication> rejectApplication(@PathVariable Integer applicationId, @RequestParam Integer reviewedByUserId,@RequestBody RejectRequest request) {
        return ResponseEntity.ok(adminApplicationService.rejectApplication(applicationId,reviewedByUserId,request.getRejectionReason()));
    }

    /**
     * Deletes an application
     */
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<void> deleteApplication(@PathVariable Integer applicationId){
        adminApplicationService.deleteAppliaiton(applicationId);
        return ResponseEntity.noContent().build();
    }
}
