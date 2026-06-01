package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Ratings;
import com.app.api.services.RatingsService;

@RestController
@RequestMapping("/api/ratings")
public class RatingsController {

    @Autowired
    private RatingsService ratingsService;

    // GET /api/ratings
    @GetMapping
    public ResponseEntity<List<Ratings>> getAllRatings() {
        return ResponseEntity.ok(ratingsService.getAllRatings());
    }

    // GET /api/ratings/1
    @GetMapping("/{id}")
    public ResponseEntity<Ratings> getRatingsById(@PathVariable int id) {
        Ratings ratings = ratingsService.getRatingById(id);
        if (ratings == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ratings);
    }

    // POST /api/ratings
    @PostMapping
    public ResponseEntity<Ratings> createRatings(@RequestBody Ratings ratings) {
        Ratings saved = ratingsService.saveRating(ratings);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/ratings/1
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