package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Badges;
import com.app.api.repositories.BadgesRepository;

/**
 * Badges service.
 */
@Service
public class BadgesService {

    @Autowired
    private BadgesRepository badgesRepository;

    /**
     * Get all badges.
     * @return list of badges
     */
    public List<Badges> getAllBadges() {
        return badgesRepository.findAll();
    }

    /**
     * Get badge by id.
     * @param id badge id
     * @return badge
     */
    public Badges getBadgeById(int id) {
        return badgesRepository.findById(id).orElse(null);
    }

    /**
     * Save badge.
     * @param badge badge
     * @return saved badge     
     */
    public Badges saveBadge(Badges badge) {
        return badgesRepository.save(badge);
    }
}
