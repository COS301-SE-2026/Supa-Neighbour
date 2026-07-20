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

import com.app.api.models.Helper;
import com.app.api.services.HelperService;
/**
 * Helper controller.
 * REST controller for Helper.
 */
@RestController
@RequestMapping("/api/helpers")
public class HelperController {

    private final HelperService helperService;


    /**
     * Basis HelperController contructor
     */
    public HelperController(HelperService helperService) {
        this.helperService = helperService;
    }

    // GET /api/helpers
    /**
     * Retrieves all helper.
     *
     * @return a list of all helpers
     */
    @GetMapping
    public ResponseEntity<List<Helper>> getAllHelpers() {
        return ResponseEntity.ok(helperService.getAllHelpers());
    }

    // GET /api/helpers/1
    /**
     * Retrieves a comment by its ID.
     *
     * @param id the helper ID
     * @return the helper if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Helper> getHelperById(@PathVariable int id) {
        Helper helper = helperService.getHelperById(id);
        if (helper == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(helper);
    }

    // POST /api/helpers
    /**
     * Creates a new helper.
     *
     * @param helper the comment to create
     * @return the created helper with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Helper> createHelper(@RequestBody Helper helper) {
        Helper saved = helperService.saveHelper(helper);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/helpers/1
    /**
     * Updates an existing helper.
     *
     * @param id the ID of the helper to update
     * @param helper the updated helper data
     * @return the updated helper if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Helper> updateHelper(@PathVariable int id, @RequestBody Helper helper) {
        Helper existing = helperService.getHelperById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Helper updated = helperService.updateHelper(id, helper);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/helpers/1
    /**
     * Deletes a helper by its ID.
     *
     * @param id the ID of the helper to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHelper(@PathVariable int id) {
        Helper existing = helperService.getHelperById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        helperService.deleteHelper(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/helpers/status/available
    /**
     * Retrieves all helpers with the specified status.
     *
     * @param status the status to filter helpers by
     * @return a list of helpers with the specified status
     */
    @GetMapping("/available")
    public ResponseEntity<List<Helper>> getAllAvailableHelpers() {
        List<Helper> helpers = helperService.findAllByStatus(true);
        return ResponseEntity.ok(helpers);
    }
}
