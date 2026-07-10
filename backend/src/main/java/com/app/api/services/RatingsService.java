package com.app.api.services;

import java.util.List;
import org.springframework.stereotype.Service;

import com.app.api.models.Ratings;
import com.app.api.repositories.RatingsRepository;

/**
 * Service layer for managing rating operations.
 * Provides CRUD functionality for Ratings entities.
 */
@Service
public class RatingsService {

    private final RatingsRepository ratingsRepository;

    public RatingsService(RatingsRepository ratingsRepository) {
        this.ratingsRepository = ratingsRepository;
    }

    // Get all
    /**
     * Retrieves all ratings from the repository.
     *
     * @return a list of all ratings
     */
    public List<Ratings> getAllRatings() {
        return ratingsRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a rating by its identifier.
     *
     * @param id the rating identifier
     * @return the rating if found, or null if no rating exists with the given id
     */
    public Ratings getRatingById(int id) {
        return ratingsRepository.findById(id).orElse(null);
    }

    // Create
    
    /**
     * Saves a new rating to the repository.
     *
     * @param rating the rating to save
     * @return the saved rating, or null if the provided rating is null
     */
    public Ratings saveRating(Ratings rating) {
        if(rating == null){
             return null;
        }

        return ratingsRepository.save(rating);
    }

    // Update
    /**
     * Updates an existing rating with the provided details.
     *
     * @param id      the identifier of the rating to update
     * @param updated the rating object containing the updated fields
     * @return the updated rating, or null if no rating exists with the given id
     */
    public Ratings updateRating(int id, Ratings updated) {
        Ratings existing = ratingsRepository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }
        
        existing.setRatingReview(updated.getRatingReview());
        existing.setTotalXpLevel(updated.getTotalXpLevel());
        existing.setCurrentGroup(updated.getCurrentGroup());

        return ratingsRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a rating by its identifier.
     *
     * @param id the identifier of the rating to delete
     */
    public void deleteRating(int id) {
        ratingsRepository.deleteById(id);
    }
}
