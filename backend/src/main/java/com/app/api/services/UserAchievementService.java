

package com.app.api.services;

import com.app.api.config.SecurityConfig;
import java.util.List;


import org.springframework.stereotype.Service;

import com.app.api.models.UserAchievement;
import com.app.api.repositories.UserAchievementRepository;

/**
 * Service layer for managing analytics operations.
 * Provides CRUD functionality for Analytics entities.
 */
@Service
public class UserAchievementService {

    
    private final SecurityConfig securityConfig;
    private final UserAchievementRepository userAchievementRepository;

    UserAchievementService(UserAchievementRepository userAchievementRepository, SecurityConfig securityConfig) {
        this.userAchievementRepository = userAchievementRepository;
        this.securityConfig = securityConfig;
    }

    // Get all
    /**
     * Retrieves all analytics records from the repository.
     *
     * @return a list of all analytics records
     */
    public List<UserAchievement> getAllUserAchievement() {
        return userAchievementRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves an analytics record by its identifier.
     *
     * @param id the analytics identifier
     * @return the analytics record if found, or null if no record exists with the given id
     */
    public UserAchievement getUserAchievementById(int id) {
        return userAchievementRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new analytics record to the repository.
     *
     * @param invitation the analytics record to save
     * @return the saved analytics record, or null if the provided analytics is null
     */
    public UserAchievement saveAchievement(UserAchievement invitation) {
        if(invitation == null) {
            return null;
        }
        return userAchievementRepository.save(invitation);
    }

    // Update
    /**
     * Updates an existing analytics record with the provided details.
     *
     * @param id      the identifier of the analytics record to update
     * @param updated the analytics object containing the updated fields
     * @return the updated analytics record, or null if no record exists with the given id
     */
    public UserAchievement updateUserAchievement(int id, UserAchievement updated) {
        UserAchievement existing = userAchievementRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setAwardedOn(updated.getAwardedOn());
        existing.setUserId(updated.getUserId());
        existing.setBadgeId(updated.getBadgeId());
        existing.setProgressCurrent(updated.getProgressCurrent());
        existing.setProgressTarget(updated.getProgressTarget());

        return userAchievementRepository.save(existing);
    }

    // Delete
    /**
     * Deletes an analytics record by its identifier.
     *
     * @param id the identifier of the analytics record to delete
     */
    public void deleteUserAchievement(int id) {
        userAchievementRepository.deleteById(id);
    }
}

