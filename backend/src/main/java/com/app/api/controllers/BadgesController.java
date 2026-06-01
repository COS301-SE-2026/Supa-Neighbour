package com.app.api.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Badges;
import com.app.api.services.BadgesService;

/**
 * Badges controller.
 */
@RestController
@RequestMapping("/api/badges")
public class BadgesController {

    @Autowired
    private BadgesService badgeService;

    /**
     * Get all badges.
     * @return badges
     */
    @GetMapping
    public List<Badges> getAllBadges() {
        return badgeService.getAllBadges();
    }

    /**
     * Get badge by id.
     * @param id badge id
     * @return badge
     */
    @GetMapping("api/badges/{id}")
    public Badges getBadgeById(@PathVariable int id) {
        return badgeService.getBadgeById(id);
    }

    /**
     * Create badge.
     * @param badge badge
     * @return saved badge
     */
    @PostMapping
    public Badges createBadge(@RequestBody Badges badge) {
        return badgeService.saveBadge(badge);
    }
}
