package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.models.Ratings;
import com.app.api.services.RatingsService;

/**
 * REST controller for Posts.
*/
@RestController
@RequestMapping("/api/ratings")
public class RatingsController {

    @Autowired
    private RatingsService ratingsService;

    // GET /api/ratings
    /**
     * Retrieves all ratings.
     *
     * @return a list of all ratings
     */
    @GetMapping
    public ResponseEntity<List<Ratings>> getAllRatings() {
        return ResponseEntity.ok(ratingsService.getAllRatings());
    }

    // GET /api/ratings/1
     /**
     * Retrieves a rating by its ID.
     *
     * @param id the rating's ID
     * @return the rating if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ratings> getRatingsById(@PathVariable int id) {
        Ratings ratings = ratingsService.getRatingById(id);
        if (ratings == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ratings);
    }

    // POST /api/ratings
    /**
     * Creates a new rating.
     *
     * @param location the rating to create
     * @return the created rating with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Ratings> createRatings(@RequestBody Ratings ratings) {
        Ratings saved = ratingsService.saveRating(ratings);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/ratings/1
     /**
     * Updates an existing rating.
     *
     * @param id the ID of the rating to update
     * @param likes the updated rating data
     * @return the updated rating if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Ratings> updateRatings(@PathVariable int id, @RequestBody Ratings ratings) {
        Ratings existing = ratingsService.getRatingById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Ratings updated = ratingsService.updateRating(id, ratings);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/ratings/1
    /**
     * Deletes a rating by its ID.
     *
     * @param id the ID of the rating to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRatings(@PathVariable int id) {
        Ratings existing = ratingsService.getRatingById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        ratingsService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
