package com.app.api.services;

import java.util.List;
import org.springframework.stereotype.Service;

import com.app.api.models.Helper;
import com.app.api.repositories.HelperRepository;

/**
 * Service layer for managing helper operations.
 * Provides CRUD functionality for Helper entities.
 */
@Service
public class HelperService {

    private final HelperRepository helperRepository;

    /**
     * Constructs theservice with its required repository dependency.
     *
     * @param helperRepository repository providing analytics data for helpers
     */
    public HelperService(HelperRepository helperRepository) {
        this.helperRepository = helperRepository;
    }
    // Get all
    /**
     * Retrieves all helpers from the repository.
     *
     * @return a list of all helpers
     */
    public List<Helper> getAllHelpers() {
        return helperRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a helper by their identifier.
     *
     * @param id the helper identifier
     * @return the helper if found, or null if no helper exists with the given id
     */
    public Helper getHelperById(int id) {
        return helperRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new helper to the repository.
     *
     * @param helper the helper to save
     * @return the saved helper, or null if the provided helper is null
     */
    public Helper saveHelper(Helper helper) {
        if(helper == null) {
            return null;
        }
        return helperRepository.save(helper);
    }

    // Update
    /**
     * Updates an existing helper with the provided details.
     *
     * @param id      the identifier of the helper to update
     * @param updated the helper object containing the updated fields
     * @return the updated helper, or null if no helper exists with the given id
     */
    public Helper updateHelper(int id, Helper updated) {
        Helper existing = helperRepository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }    
        
        existing.setUserid(updated.getUserid());
        existing.setTaskTypeid(updated.getTaskTypeid());
        existing.setBadgeid(updated.getBadgeid());
        existing.setHelperXp(updated.getHelperXp());
        existing.setAvailable(updated.isAvailable());

        return helperRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a helper by their identifier.
     *
     * @param id the identifier of the helper to delete
     */
    public void deleteHelper(int id) {
        helperRepository.deleteById(id);
    }

    //findAllByStatus
    /**
     * Retrieves all helpers with the specified status.
     *     * @param status the status to filter helpers by
     * @return a list of helpers with the specified status
     */
    public List<Helper> findAllByStatus(boolean available) {
        return helperRepository.findByAvailable(available);
    }
}
