package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Helper;
import com.app.api.repositories.HelperRepository;

/**
 * Helper service.
 */
@Service
public class HelperService {

    @Autowired
    private HelperRepository helperRepository;

    /**
     * Get all helpers.
     * @return list of helpers
     */
    public Iterable<Helper> getAllHelpers() {
        return helperRepository.findAll();
    }

    /**
     * Get helper by id.
     * @param id helper id
     * @return helper
     */
    public Helper getHelperById(int id) {
        return helperRepository.findById(id).orElse(null);
    }

    /**
     * Save helper.
     * @param helper helper
     * @return saved helper
     */
    public Helper saveHelper(Helper helper) {
        return helperRepository.save(helper);
    }
}
