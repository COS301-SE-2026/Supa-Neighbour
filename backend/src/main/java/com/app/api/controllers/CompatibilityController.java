package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Compatibility;
import com.app.api.services.CompatibilityService;

@RestController
@RequestMapping("/api/compatibility")
public class CompatibilityController {

    @Autowired
    private CompatibilityService compatibilityService;

    // GET /api/compatibility
    @GetMapping
    public ResponseEntity<List<Compatibility>> getAllCompatibility() {
        return ResponseEntity.ok(compatibilityService.getAllCompatibility());
    }

    // GET /api/compatibility/1
    @GetMapping("/{id}")
    public ResponseEntity<Compatibility> getCompatibilityById(@PathVariable int id) {
        Compatibility compatibility = compatibilityService.getCompatibilityById(id);
        if (compatibility == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(compatibility);
    }

    // POST /api/compatibility
    @PostMapping
    public ResponseEntity<Compatibility> createCompatibility(@RequestBody Compatibility compatibility) {
        Compatibility saved = compatibilityService.saveCompatibility(compatibility);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/compatibility/1
    @PutMapping("/{id}")
    public ResponseEntity<Compatibility> updateCompatibility(@PathVariable int id, @RequestBody Compatibility compatibility) {
        Compatibility existing = compatibilityService.getCompatibilityById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Compatibility updated = compatibilityService.updateCompatibility(id, compatibility);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/compatibility/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompatibility(@PathVariable int id) {
        Compatibility existing = compatibilityService.getCompatibilityById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        compatibilityService.deleteCompatibility(id);
        return ResponseEntity.noContent().build();
    }
}