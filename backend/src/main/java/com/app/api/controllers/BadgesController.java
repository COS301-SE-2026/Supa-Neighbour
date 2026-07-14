package com.app.api.controllers;

import java.util.List;


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

import com.app.api.models.Badges;
import com.app.api.services.BadgesService;


/**
 * REST controller for managing badges.
 */
@RestController
@RequestMapping("/api/badges")
public class BadgesController {


    private final BadgesService badgesService;

    /**
     * Basis Contructor for the Badges Controller
     * @param badgesService service for the badges
     */
    public BadgesController(BadgesService badgesService) {
        this.badgesService = badgesService;
    }

    // GET /api/badges
    /**
     * Get all badges.
     *
     * @return a list of all badges
     */
    @GetMapping
    public ResponseEntity<List<Badges>> getAllBadges() {
        return ResponseEntity.ok(badgesService.getAllBadges());
    }

    // GET /api/badges/1
    /**
     * Get a single badge by its ID.
     *
     * @param id the badge ID
     * @return the matching badge, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Badges> getBadgeById(@PathVariable int id) {
        Badges badge = badgesService.getBadgesById(id);
        if (badge == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(badge);
    }

    // POST /api/badges
     /**
     * Create a new badge.
     *
     * @param badge the badge to create
     * @return the saved badge
     */
    @PostMapping
    public ResponseEntity<Badges> createBadge(@RequestBody Badges badge) {
        Badges saved = badgesService.saveBadges(badge);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/badges/1
    /**
     * Update an existing badge.
     *
     * @param id the ID of the badge to update
     * @param badge the updated badge data
     * @return the updated badge, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Badges> updateBadge(@PathVariable int id, @RequestBody Badges badge) {
        Badges existing = badgesService.getBadgesById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Badges updated = badgesService.updateBadges
        (id, badge);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/badges/1
    /**
     * Delete a badge by its ID.
     *
     * @param id the ID of the badge to delete
     * @return 204 No Content, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable int id) {
        Badges existing = badgesService.getBadgesById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        badgesService.deleteBadges(id);
        return ResponseEntity.noContent().build();
    }
}
