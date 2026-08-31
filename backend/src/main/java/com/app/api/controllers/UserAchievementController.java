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

import com.app.api.models.UserAchievement;
import com.app.api.services.UserAchievementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * UserAchievementController
 */
@RestController
@RequestMapping("/api/userAchievement")
@Tag(name = "User Achievements", description = "Operations for managing user achievement records")
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
    @Operation(summary = "Get all user achievements", description = "Retrieves a list of all user achievement records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user achievements")
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
    @Operation(summary = "Get user achievement by ID", description = "Retrieves a single user achievement record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User achievement found"),
        @ApiResponse(responseCode = "404", description = "User achievement not found", content = @Content)
    })
    public ResponseEntity<UserAchievement> getUserAchievementById(
        @Parameter(description = "ID of the user achievement to retrieve", example = "1")
        @PathVariable int id
    ) {
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
    @Operation(summary = "Create a new user achievement", description = "Creates a new user achievement record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User achievement created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user achievement data", content = @Content)
    })
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
    @Operation(summary = "Update a user achievement", description = "Updates an existing user achievement record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User achievement updated successfully"),
        @ApiResponse(responseCode = "404", description = "User achievement not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid user achievement data", content = @Content)
    })
    public ResponseEntity<UserAchievement> updateAchievements(
        @Parameter(description = "ID of the user achievement to update", example = "1")
        @PathVariable int id,
        @RequestBody UserAchievement achievement
    ) {
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
    @Operation(summary = "Delete a user achievement", description = "Deletes a user achievement record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User achievement deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "User achievement not found", content = @Content)
    })
    public ResponseEntity<Void> deleteAchievements(
        @Parameter(description = "ID of the user achievement to delete", example = "1")
        @PathVariable int id
    ) {
        UserAchievement existing = userAchievementService.getUserAchievementById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        userAchievementService.deleteUserAchievement(id);
        return ResponseEntity.noContent().build();
    }
}
