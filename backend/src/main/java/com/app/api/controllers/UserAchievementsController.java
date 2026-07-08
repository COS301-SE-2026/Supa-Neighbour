package com.app.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.app.api.models.UserAchievement;
import com.app.api.services.UserAchievementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/userAchievement")
public class UserAchievementsController {

    @Autowired
    private UserAchievementService userAchievementService;

    // GET /api/analytics
    /**
     * Retrieves all analytics records.
     *
     * @return a response containing the list of analytics records
     */
    @GetMapping
    public ResponseEntity<List<UserAchievement>> getAllUserAchievement() {
        return ResponseEntity.ok(userAchievementService.getAllUserAchievement());
    }

    // GET /api/analytics/1
    /**
     * Retrieves an analytics record by its ID.
     *
     * @param id the analytics ID
     * @return a response containing the analytics record, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserAchievement> getAnalyticsById(@PathVariable int id) {
        UserAchievement achievement = userAchievementService.getUserAchievementById(id);
        if (achievement == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(achievement);
    }

    // POST /api/analytics
    /**
     * Creates a new analytics record.
     *
     * @param analytics the analytics data to create
     * @return a response containing the created analytics record
     */
    @PostMapping
    public ResponseEntity<UserAchievement> createAnalytics(@RequestBody UserAchievement achievement) {
        UserAchievement saved = userAchievementService.saveAchievement(achievement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/analytics/1
    /**
     * Updates an existing analytics record.
     *
     * @param id the analytics ID
     * @param analytics the updated analytics data
     * @return a response containing the updated analytics record, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserAchievement> updateAchievements(@PathVariable int id, @RequestBody UserAchievement achievement) {
        UserAchievement existing = userAchievementService.getUserAchievementById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        UserAchievement updated = userAchievementService.updateUserAchievement(id, achievement);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/analytics/1
    /**
     * Deletes an analytics record by its ID.
     *
     * @param id the analytics ID
     * @return a response with no content, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAchievements(@PathVariable int id) {
        UserAchievement existing = userAchievementService.getUserAchievementById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        userAchievementService.deleteAnalytics(id);
        return ResponseEntity.noContent().build();
    }
}