package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Dependent;
import com.app.api.services.DependentService;

@RestController
@RequestMapping("/api/dependents")
public class DependentController {

    @Autowired
    private DependentService dependentService;

    // GET /api/dependents
    @GetMapping
    public ResponseEntity<List<Dependent>> getAllDependents() {
        return ResponseEntity.ok(dependentService.getAllDependents());
    }

    // GET /api/dependents/1
    @GetMapping("/{id}")
    public ResponseEntity<Dependent> getDependentById(@PathVariable int id) {
        Dependent dependent = dependentService.getDependentById(id);
        if (dependent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dependent);
    }

    // POST /api/dependents
    @PostMapping
    public ResponseEntity<Dependent> createDependent(@RequestBody Dependent dependent) {
        Dependent saved = dependentService.saveDependent(dependent);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dependent> updateDependent(@PathVariable int id, @RequestBody Dependent dependent) {
        Dependent existing = dependentService.getDependentById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Dependent updated = dependentService.updateDependent(id, dependent);
        return ResponseEntity.ok(updated);
    }

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