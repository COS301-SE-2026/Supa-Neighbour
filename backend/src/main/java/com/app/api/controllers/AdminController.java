package com.app.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.models.Admin;
import com.app.api.services.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admins")
@Tag(name = "Admins", description = "Operations for managing administrators")
public class AdminController {

    private final AdminService adminService;

    /**
     * Constructs the controller with its required service dependency.
     *
     * @param adminService service providing analytics data for admins
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // GET /api/admins
    /**
     * Retrieves all admins.
     *
     * @return a response containing the list of admins
     */
    @GetMapping
    @Operation(summary = "Get all admins", description = "Retrieves a list of all administrators")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved admins")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    // GET /api/admins/1
    /**
     * Retrieves an admin by their ID.
     *
     * @param id the admin ID
     * @return a response containing the admin, or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get admin by ID", description = "Retrieves a single administrator by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin found"),
        @ApiResponse(responseCode = "404", description = "Admin not found", content = @Content)
    })
    public ResponseEntity<Admin> getAdminById(
        @Parameter(description = "ID of the admin to retrieve", example = "1")
        @PathVariable int id
    ) {
        Admin admin = adminService.getAdminById(id);
        if (admin == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(admin);
    }

    // POST /api/admins
    /**
     * Creates a new admin.
     *
     * @param admin the admin to create
     * @return a response containing the created admin
     */
    @PostMapping
    @Operation(summary = "Create a new admin", description = "Creates a new administrator")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Admin created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid admin data", content = @Content)
    })
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin saved = adminService.saveAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/admins/1
    /**
     * Updates an existing admin.
     *
     * @param id the admin ID
     * @param admin the updated admin details
     * @return a response containing the updated admin, or 404 if not found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an admin", description = "Updates an existing administrator by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin updated successfully"),
        @ApiResponse(responseCode = "404", description = "Admin not found", content = @Content)
    })
    public ResponseEntity<Admin> updateAdmin(
        @Parameter(description = "ID of the admin to update", example = "1")
        @PathVariable int id,
        @RequestBody Admin admin
    ) {
        Admin existing = adminService.getAdminById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Admin updated = adminService.updateAdmin(id, admin);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/admins/1
    /**
     * Deletes an admin by their ID.
     *
     * @param id the admin ID
     * @return a response with no content, or 404 if not found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an admin", description = "Deletes an administrator by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Admin deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Admin not found", content = @Content)
    })
    public ResponseEntity<Void> deleteAdmin(
        @Parameter(description = "ID of the admin to delete", example = "1")
        @PathVariable int id
    ) {
        Admin existing = adminService.getAdminById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
