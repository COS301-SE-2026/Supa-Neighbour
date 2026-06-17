package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Compatibility;
import com.app.api.repositories.CompatibilityRepository;

@Service
public class CompatibilityService {

    @Autowired
    private CompatibilityRepository compatibilityRepository;

    // Get all
    public List<Compatibility> getAllCompatibility() {
        return compatibilityRepository.findAll();
    }

    // Get by id
    public Compatibility getCompatibilityById(int id) {
        return compatibilityRepository.findById(id).orElse(null);
    }

    // Create
    public Compatibility saveCompatibility(Compatibility compatibility) {
        if(compatibility == null) return null;
        return compatibilityRepository.save(compatibility);
    }

    // Update
    public Compatibility updateCompatibility(int id, Compatibility updated) {
        Compatibility existing = compatibilityRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setCompatibilityScore(updated.getCompatibilityScore());
        existing.setCompatibilityColour(updated.getCompatibilityColour());
        existing.setDependentid(updated.getDependentid());
        existing.setHelperid(updated.getHelperid());

        return compatibilityRepository.save(existing);
    }

    // Delete
    public void deleteCompatibility(int id) {
        compatibilityRepository.deleteById(id);
    }
}