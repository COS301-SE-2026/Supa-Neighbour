package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Admin;
import com.app.api.repositories.AdminRepository;

/**
 * Admin service.
 */
@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    /**
     * Get all admins.
     * @return list of admins
     */
    public Iterable<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    /**
     * Get admin by id.
     * @param id admin id
     * @return admin
     */
    public Admin getAdminById(int id) {
        return adminRepository.findById(id).orElse(null);
    }

    /**
     * Save admin.
     * @param admin admin
     * @return saved admin
     */
    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }
}
