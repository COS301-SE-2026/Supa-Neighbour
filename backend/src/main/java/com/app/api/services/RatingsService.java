package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Ratings;
import com.app.api.repositories.RatingsRepository;

/**
 * Ratings service.
 */
@Service
public class RatingsService {

    @Autowired
    private RatingsRepository ratingsRepository;

    /**
     * Get all ratings.
     * @return list of ratings
     */
    public Iterable<Ratings> getAllRatings() {
        return ratingsRepository.findAll();
    }

    /**
     * Get rating by id.
     * @param id rating id
     * @return rating
     */
    public Ratings getRatingById(int id) {
        return ratingsRepository.findById(id).orElse(null);
    }

    /**
     * Save rating.
     * @param rating rating
     * @return saved rating 
     */
    public Ratings saveRating(Ratings rating) {
        return ratingsRepository.save(rating);
    }
}
