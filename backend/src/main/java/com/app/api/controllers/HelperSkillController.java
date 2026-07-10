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

import com.app.api.models.HelperSkill;
import com.app.api.services.HelperSkillService;
/**
 * HelperSkill controller.
 * REST controller for HelperSkill.
 */
@RestController
@RequestMapping("/api/helper-skills")
public class HelperSkillController {
        private final HelperSkillService helperSkillService;

    public HelperSkillController(HelperSkillService helperSkillService) {
        this.helperSkillService = helperSkillService;
    }

    // GET /api/helper-skills   
/**
     * Retrieves all helper skills.
     *
     * @return a list of all helper skills
     */
    @GetMapping
    public ResponseEntity<List<HelperSkill>> getAllHelperSkills() {
        return ResponseEntity.ok(helperSkillService.getAllHelpersSkills());
    }

    // GET /api/helper-skills/1
    /**
     * Retrieves a helper skill by its ID.
     * @param id the helper skill ID
     * @return the helper skill if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<HelperSkill> getHelperSkillById(@PathVariable int id) {
        HelperSkill helperSkill = helperSkillService.getHelperSkillById(id);
        if (helperSkill == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(helperSkill);
    }

    // POST /api/helper-skills
    /**
     * Creates a new helper skill.
     * @param helperSkill the helper skill to create
     * @return the created helper skill with a 201 Created status, or 400 Bad Request if the helper skill is null
     */
    @PostMapping
    public ResponseEntity<HelperSkill> createHelperSkill(@RequestBody HelperSkill helperSkill) {
        HelperSkill created = helperSkillService.saveHelperSkill(helperSkill);
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/helper-skills/1
    /**
     * Updates an existing helper skill.
     * @param id         the helper skill ID
     * @param updated    the updated helper skill data
     * @return the updated helper skill if successful, 404 Not Found if the helper skill doesn't exist, or 400 Bad Request if the provided helper skill is null
     */
    @PutMapping("/{id}")
    public ResponseEntity<HelperSkill> updateHelperSkill(@PathVariable int id, @RequestBody HelperSkill updated) {
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        HelperSkill existing = helperSkillService.getHelperSkillById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        HelperSkill updatedSkill = helperSkillService.updateHelperSkill(id, updated);
        return ResponseEntity.ok(updatedSkill);
    }

    // DELETE /api/helper-skills/1
    /**
     * Deletes a helper skill by its ID.
     * @param id the helper skill ID
     * @return 204 No Content if successful, or 404 Not Found if the helper skill doesn't exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHelperSkill(@PathVariable int id) {
        boolean deleted = helperSkillService.deleteHelperSkill(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
