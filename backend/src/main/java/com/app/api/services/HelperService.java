package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Helper;
import com.app.api.repositories.HelperRepository;

@Service
public class HelperService {

    @Autowired
    private HelperRepository helperRepository;

    // Get all
    public List<Helper> getAllHelpers() {
        return helperRepository.findAll();
    }

    // Get by id
    public Helper getHelperById(int id) {
        return helperRepository.findById(id).orElse(null);
    }

    // Create
    public Helper saveHelper(Helper helper) {
        return helperRepository.save(helper);
    }

    // Update
    public Helper updateHelper(int id, Helper updated) {
        Helper existing = helperRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setUserid(updated.getUserid());
        existing.setTaskTypeid(updated.getTaskTypeid());

        return helperRepository.save(existing);
    }

    // Delete
    public void deleteHelper(int id) {
        helperRepository.deleteById(id);
    }
}