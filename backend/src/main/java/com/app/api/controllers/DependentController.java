package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Dependent;
import com.app.api.services.DependentService;

/**
 * Dependent controller.
 */
@RestController
@RequestMapping("api/dependents")
public class DependentController {

    @Autowired
    private DependentService dependentService;

    /**
     * Get all dependents.
     * @return dependents
     */
    @GetMapping
    public Iterable<Dependent> getAllDependents() {
        return dependentService.getAllDependents();
    }

    /**
     * Get dependent by id.
     * @param id dependent id
     * @return dependent
     */
    @GetMapping("api/dependents/{id}")
    public Dependent getDependentById(@PathVariable int id) {
        return dependentService.getDependentById(id);
    }

    /**
     * Create dependent.
     * @param dependent dependent
     * @return saved dependent
     */
    @PostMapping
    public Dependent createDependent(@RequestBody Dependent dependent) {
        return dependentService.saveDependent(dependent);
    }
}
