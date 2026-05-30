package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Helper;
import com.app.api.services.HelperService;

/**
 * Helper controller.
 */
@RestController
@RequestMapping("api/helpers")
public class HelperController {

    @Autowired
    private HelperService helperService;

    /**
     * Get all helpers.
     * @return helpers
     */
    @GetMapping
    public List<Helper> getAllHelpers() {
        return helperService.getAllHelpers();
    }

    /**
     * Get helper by id.
     * @param id helper id
     * @return helper
     */
    @GetMapping("api/helpers/{id}")
    public Helper getHelperById(@PathVariable int id) {
        return helperService.getHelperById(id);
    }

    /**
     * Create helper.
     * @param helper helper
     * @return saved helper
     */
    @PostMapping
    public Helper createHelper(@RequestBody Helper helper) {
        return helperService.saveHelper(helper);
    }
}
