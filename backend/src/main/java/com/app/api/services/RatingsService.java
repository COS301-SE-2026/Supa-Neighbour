package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Ratings;
import com.app.api.repositories.RatingsRepository;

@Service
public class RatingsService {

    @Autowired
    private RatingsRepository ratingsRepository;

    // Get all
    public List<Ratings> getAllRatings() {
        return ratingsRepository.findAll();
    }

    // Get by id
    public Ratings getRatingById(int id) {
        return ratingsRepository.findById(id).orElse(null);
    }

    // Create
    public Ratings saveRating(Ratings rating) {
        if(rating == null) return null;
        return ratingsRepository.save(rating);
    }

    // Update
    public Ratings updateRating(int id, Ratings updated) {
        Ratings existing = ratingsRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setRatingReview(updated.getRatingReview());
        existing.setTotalXpLevel(updated.getTotalXpLevel());
        existing.setCurrentGroup(updated.getCurrentGroup());

        return ratingsRepository.save(existing);
    }

    // Delete
    public void deleteRating(int id) {
        ratingsRepository.deleteById(id);
    }
}