package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Compatibility;
import com.app.api.repositories.CompatibilityRepository;

/**
 * Compatibility service.
 */
@Service
public class CompatibilityService {

    @Autowired
    private CompatibilityRepository compatibilityRepository;

    /**
     * Get all compatibilities.
     * @return list of compatibilities
     */
    public Iterable<Compatibility> getAllCompatibilities() {
        return compatibilityRepository.findAll();
    }

    /**
     * Get compatibility by id.
     * @param id compatibility id
     * @return compatibility
     */
    public Compatibility getCompatibilityById(int id) {
        return compatibilityRepository.findById(id).orElse(null);
    }

    /**
     * Save compatibility.
     * @param compatibility compatibility
     * @return saved compatibility
     */
    public Compatibility saveCompatibility(Compatibility compatibility) {
        return compatibilityRepository.save(compatibility);
    }
}
