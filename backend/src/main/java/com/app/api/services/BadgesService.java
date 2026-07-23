package com.app.api.services;

import java.util.List;
import org.springframework.stereotype.Service;

import com.app.api.models.Badges;
import com.app.api.repositories.BadgesRepository;

/**
 * Service layer for managing badge operations.
 * Provides CRUD functionality for Badges entities.
 */
@Service
public class BadgesService {


    private final BadgesRepository badgesRepository;

    /**
     * Constructs the repository with its required service dependency.
     *
     * @param badgesRepository repository providing analytics data for badges
     */
    public BadgesService(BadgesRepository badgesRepository) {
        this.badgesRepository = badgesRepository;
    }

    // Get all
    /**
     * Retrieves all badges from the repository.
     *
     * @return a list of all badges
     */
    public List<Badges> getAllBadges() {
        return badgesRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a badge by its identifier.
     *
     * @param id the badge identifier
     * @return the badge if found, or null if no badge exists with the given id
     */
    public Badges getBadgesById(int id) {
        return badgesRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new badge to the repository.
     *
     * @param badges the badge to save
     * @return the saved badge, or null if the provided badge is null
     */
    public Badges saveBadges(Badges badges) {
        if(badges == null) {
            return null;
        }
        
        return badgesRepository.save(badges);
    }

    // Update
    /**
     * Updates an existing badge with the provided details.
     *
     * @param id      the identifier of the badge to update
     * @param updated the badge object containing the updated fields
     * @return the updated badge, or null if no badge exists with the given id
     */
    public Badges updateBadges(int id, Badges updated) {
        Badges existing = badgesRepository.findById(id).orElse(null);
  
        if (existing == null) {
            return null;
        }

        existing.setXpReward(updated.getXpReward());
        existing.setBadge_description(updated.getBadge_description());
        existing.setBadgeDescription(updated.getBadgeDescription());
        existing.setBadgeName(updated.getBadgeName());
        existing.setRatingid(updated.getRatingid());
        existing.setIsSpecialist(updated.getIsSpecialist());

        return badgesRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a badge by its identifier.
     *
     * @param id the identifier of the badge to delete
     */
    public void deleteBadges(int id) {
        badgesRepository.deleteById(id);
    }
}
