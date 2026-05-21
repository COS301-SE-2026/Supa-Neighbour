package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Ratings;
import com.app.api.services.RatingsService;

/**
 * Ratings controller.
 */
@RestController
@RequestMapping("api/ratings")
public class RatingsController {

    @Autowired
    private RatingsService ratingsService;

    /**
     * Get all ratings.
     * @return ratings
     */
    @GetMapping
    public Iterable<Ratings> getAllRatings() {
        return ratingsService.getAllRatings();
    }

    /**
     * Get rating by id.
     * @param id rating id
     * @return rating
     */
    @GetMapping("api/ratings/{id}")
    public Ratings getRatingById(@PathVariable int id) {
        return ratingsService.getRatingById(id);
    }

    /**
     * Create rating.
     * @param rating rating
     * @return saved rating
     */
    @PostMapping
    public Ratings createRating(@RequestBody Ratings rating) {
        return ratingsService.saveRating(rating);
    }
}
