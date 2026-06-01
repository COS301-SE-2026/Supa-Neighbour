package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Helper;
import com.app.api.services.HelperService;
/**
 * Helper controller.
 */
@RestController
@RequestMapping("/api/helpers")
public class HelperController {

    @Autowired
    private HelperService helperService;

    // GET /api/helpers
    @GetMapping
    public ResponseEntity<List<Helper>> getAllHelpers() {
        return ResponseEntity.ok(helperService.getAllHelpers());
    }

    // GET /api/helpers/1
    @GetMapping("/{id}")
    public ResponseEntity<Helper> getHelperById(@PathVariable int id) {
        Helper helper = helperService.getHelperById(id);
        if (helper == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(helper);
    }

    // POST /api/helpers
    @PostMapping
    public ResponseEntity<Helper> createHelper(@RequestBody Helper helper) {
        Helper saved = helperService.saveHelper(helper);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/helpers/1
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHelper(@PathVariable int id) {
        Helper existing = helperService.getHelperById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        helperService.deleteHelper(id);
        return ResponseEntity.noContent().build();
    }
}