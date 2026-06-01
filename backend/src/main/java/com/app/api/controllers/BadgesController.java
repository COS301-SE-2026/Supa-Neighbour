package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Badges;
import com.app.api.services.BadgesService;

@RestController
@RequestMapping("/api/badges")
public class BadgesController {

    @Autowired
    private BadgesService badgesService;

    // GET /api/badges
    @GetMapping
    public ResponseEntity<List<Badges>> getAllBadges() {
        return ResponseEntity.ok(badgesService.getAllBadges());
    }

    // GET /api/badges/1
    @GetMapping("/{id}")
    public ResponseEntity<Badges> getBadgeById(@PathVariable int id) {
        Badges badge = badgesService.getBadgesById(id);
        if (badge == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(badge);
    }

    // POST /api/badges
    @PostMapping
    public ResponseEntity<Badges> createBadge(@RequestBody Badges badge) {
        Badges saved = badgesService.saveBadges(badge);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/badges/1
    @PutMapping("/{id}")
    public ResponseEntity<Badges> updateBadge(@PathVariable int id, @RequestBody Badges badge) {
        Badges existing = badgesService.getBadgesById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Badges updated = badgesService.updateBadges(id, badge);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/badges/1
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