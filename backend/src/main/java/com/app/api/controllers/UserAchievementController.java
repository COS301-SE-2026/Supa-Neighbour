package com.app.api.controllers;

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

/**
 * UserAchievementController
 */
@RestController
@RequestMapping("/api/userAchievement")
public class UserAchievementController {

    
    private final UserAchievementService userAchievementService;

    /**
     * Constructs the controller with its required service dependency.
     *
     * @param userAchievementService service providing analytics data for dependents
     */
    public UserAchievementController(UserAchievementService userAchievementService) {
        this.userAchievementService = userAchievementService;
    }

    // GET /api/userAchievement
    /**
     * Retrieves all user achievement records.
     *
     * @return a response containing the list of user achievement records
     */
    @GetMapping
    public ResponseEntity<List<UserAchievement>> getAllUserAchievement() {
        return ResponseEntity.ok(userAchievementService.getAllUserAchievement());
    }

    // GET /api/userAchievement/1
    /**
     * Retrieves a user achievement record by its ID.
     *
     * @param id the user achievement ID
     * @return a response containing the user achievement record, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserAchievement> getUserAchievementById(@PathVariable int id) {
        UserAchievement achievement = userAchievementService.getUserAchievementById(id);
        if (achievement == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(achievement);
    }

    // POST /api/userAchievement
    /**
     * Creates a new user achievement record.
     *
     * @param userAchievement the user achievement data to create
     * @return a response containing the created user achievement record
     */
    @PostMapping
    public ResponseEntity<UserAchievement> createAnalytics(@RequestBody UserAchievement achievement) {
        UserAchievement saved = userAchievementService.saveAchievement(achievement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/userAchievement/1
    /**
     * Updates an existing user achievement record.
     *
     * @param id the user achievement ID
     * @param userAchievement the updated user achievement data
     * @return a response containing the updated user achievement record, or 404 if not found
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

    // DELETE /api/userAchievement/1
    /**
     * Deletes a user achievement record by its ID.
     *
     * @param id the user achievement ID
     * @return a response with no content, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAchievements(@PathVariable int id) {
        UserAchievement existing = userAchievementService.getUserAchievementById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        userAchievementService.deleteUserAchievement(id);
        return ResponseEntity.noContent().build();
    }
}
