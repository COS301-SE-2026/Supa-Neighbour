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

import com.app.api.models.Dependent;
import com.app.api.services.DependentService;

/**
 * REST controller for managing the dependant
 */
@RestController
@RequestMapping("/api/dependents")
public class DependentController {

    @Autowired
    private DependentService dependentService;

    // GET /api/dependents
    /**
     * Retrieves all comments.
     *
     * @return a list of all dependants
     */
    @GetMapping
    public ResponseEntity<List<Dependent>> getAllDependents() {
        return ResponseEntity.ok(dependentService.getAllDependents());
    }

    // GET /api/dependents/1
    /**
     * Retrieves a comment by its ID.
     *
     * @param id the dependent ID
     * @return the dependent if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Dependent> getDependentById(@PathVariable int id) {
        Dependent dependent = dependentService.getDependentById(id);
        if (dependent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dependent);
    }

    // POST /api/dependents
    /**
     * Creates a new dependent.
     *
     * @param dependent the comment to create
     * @return the created dependent with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Dependent> createDependent(@RequestBody Dependent dependent) {
        Dependent saved = dependentService.saveDependent(dependent);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Updates an existing dependent.
     *
     * @param id the ID of the comment to update
     * @param dependent the updated dependent data
     * @return the updated dependent if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Dependent> updateDependent(@PathVariable int id, @RequestBody Dependent dependent) {
        Dependent existing = dependentService.getDependentById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Dependent updated = dependentService.updateDependent(id, dependent);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a dependent by its ID.
     *
     * @param id the ID of the dependent to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDependent(@PathVariable int id) {
        Dependent existing = dependentService.getDependentById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        dependentService.deleteDependent(id);
        return ResponseEntity.noContent().build();
    }
}
