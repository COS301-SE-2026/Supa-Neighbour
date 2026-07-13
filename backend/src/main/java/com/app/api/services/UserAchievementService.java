

package com.app.api.services;
import java.util.List;


import org.springframework.stereotype.Service;

import com.app.api.models.UserAchievement;
import com.app.api.repositories.UserAchievementRepository;

/**
 * Service layer for managing user achievement operations.
 * Provides CRUD functionality for UserAchievement entities.
 */
@Service
public class UserAchievementService {

    private final UserAchievementRepository userAchievementRepository;

    UserAchievementService(UserAchievementRepository userAchievementRepository) {
        this.userAchievementRepository = userAchievementRepository;

    }

    // Get all
    /**
     * Retrieves all user achievement records from the repository.
     *
     * @return a list of all user achievement records
     */
    public List<UserAchievement> getAllUserAchievement() {
        return userAchievementRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a user achievement record by its identifier.
     *
     * @param id the user achievement identifier
     * @return the user achievement record if found, or null if no record exists with the given id
     */
    public UserAchievement getUserAchievementById(int id) {
        return userAchievementRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new user achievement record to the repository.
     *
     * @param invitation the user achievement record to save
     * @return the saved user achievement record, or null if the provided user achievement is null
     */
    public UserAchievement saveAchievement(UserAchievement invitation) {
        if(invitation == null) {
            return null;
        }
        return userAchievementRepository.save(invitation);
    }

    // Update
    /**
     * Updates an existing user achievement record with the provided details.
     *
     * @param id      the identifier of the user achievement record to update
     * @param updated the user achievement object containing the updated fields
     * @return the updated user achievement record, or null if no record exists with the given id
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
     * Deletes a user achievement record by its identifier.
     *
     * @param id the identifier of the user achievement record to delete
     */
    public void deleteUserAchievement(int id) {
        userAchievementRepository.deleteById(id);
    }
}

