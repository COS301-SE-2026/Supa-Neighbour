package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Compatibility;
import com.app.api.services.CompatibilityService;

/**
 * Compatibility controller.
 */
@RestController
@RequestMapping("api/compatibilities")
public class CompatibilityController {

    @Autowired
    private CompatibilityService compatibilityService;

    /**
     * Get all compatibilities.
     * @return compatibilities
     */
    @GetMapping
    public Iterable<Compatibility> getAllCompatibilities() {
        return compatibilityService.getAllCompatibilities();
    }

    /**
     * Get compatibility by id.
     * @param id compatibility id
     * @return compatibility
     */
    @GetMapping("api/compatibilities/{id}")
    public Compatibility getCompatibilityById(@PathVariable int id) {
        return compatibilityService.getCompatibilityById(id);
    }

    /**
     * Create compatibility.
     * @param compatibility compatibility
     * @return saved compatibility
     */
    @PostMapping
    public Compatibility createCompatibility(@RequestBody Compatibility compatibility) {
        return compatibilityService.saveCompatibility(compatibility);
    }
}
