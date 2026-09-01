package com.app.api.services;

import java.util.List;


import org.springframework.stereotype.Service;

import com.app.api.models.Admin;
import com.app.api.repositories.AdminRepository;

/**
 * Service layer for managing admin operations.
 * Provides CRUD functionality for Admin entities.
 */
@Service
public class AdminService {


    private final AdminRepository adminRepository;

    /**
     * Constructs the service with its required repository dependency.
     *
     * @param adminRepository repository providing analytics data for posts
     */
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }
    // Get all
    /**
     * Retrieves all admins from the repository.
     *
     * @return a list of all admins
     */
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // Get by id
    
    /**
     * Retrieves an admin by their identifier.
     *
     * @param id the admin identifier
     * @return the admin if found, or null if no admin exists with the given id
     */
    public Admin getAdminById(int id) {
        return adminRepository.findById(id).orElse(null);
    }

    // Create
    
    /**
     * Saves a new admin to the repository.
     *
     * @param admin the admin to save
     * @return the saved admin, or null if the provided admin is null
     */
    public Admin saveAdmin(Admin admin) {
        if(admin == null) {
            return null;
        }
        return adminRepository.save(admin);
    }

    // Update
    
    /**
     * Updates an existing admin with the provided details.
     *
     * @param id      the identifier of the admin to update
     * @param updated the admin object containing the updated fields
     * @return the updated admin, or null if no admin exists with the given id
     */
    public Admin updateAdmin(int id, Admin updated) {
        Admin existing = adminRepository.findById(id).orElse(null);
        
        if(existing == null) {
            return null;
        }

        existing.setUserid(updated.getUserid());
        existing.setAdminaccesslevel(updated.getAdminaccesslevel());
        existing.setAdmincreatedate(updated.getAdmincreatedate());

        return adminRepository.save(existing);
    }

    // Delete
    /**
     * Deletes an admin by their identifier.
     *
     * @param id the identifier of the admin to delete
     */
    public void deleteAdmin(int id) {
        adminRepository.deleteById(id);
    }
}
