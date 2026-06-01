package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Admin;
import com.app.api.repositories.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    // Get all
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // Get by id
    public Admin getAdminById(int id) {
        return adminRepository.findById(id).orElse(null);
    }

    // Create
    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // Update
    public Admin updateAdmin(int id, Admin updated) {
        Admin existing = adminRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setUserid(updated.getUserid());
        existing.setAdminaccesslevel(updated.getAdminaccesslevel());

        return adminRepository.save(existing);
    }

    // Delete
    public void deleteAdmin(int id) {
        adminRepository.deleteById(id);
    }
}