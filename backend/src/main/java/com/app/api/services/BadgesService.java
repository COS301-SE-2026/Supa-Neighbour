package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Badges;
import com.app.api.repositories.BadgesRepository;

@Service
public class BadgesService {

    @Autowired
    private BadgesRepository badgesRepository;

    // Get all
    public List<Badges> getAllBadges() {
        return badgesRepository.findAll();
    }

    // Get by id
    public Badges getBadgesById(int id) {
        return badgesRepository.findById(id).orElse(null);
    }

    // Create
    public Badges saveBadges(Badges badges) {
        if(badges == null) return null;
        return badgesRepository.save(badges);
    }

    // Update
    public Badges updateBadges(int id, Badges updated) {
        Badges existing = badgesRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setXpReward(updated.getXpReward());
        existing.setDescription(updated.getDescription());
        existing.setBadgeName(updated.getBadgeName());
        existing.setRatingid(updated.getRatingid());

        return badgesRepository.save(existing);
    }

    // Delete
    public void deleteBadges(int id) {
        badgesRepository.deleteById(id);
    }
}