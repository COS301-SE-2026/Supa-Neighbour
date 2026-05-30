package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Likes;
import com.app.api.repositories.LikesRepository;

/**
 * Dependent analytics service.
 */
@Service
public class LikesService {

    @Autowired
    private LikesRepository likesRepository;

    /**
     * Get all dependent analytics.
     * @return list of dependent analytics
     */
    public List<Likes> getAllLikes() {
        return likesRepository.findAll();
    }

    /**
     * Get dependent analytics by id.
     * @param id dependent analytics id
     * @return dependent analytics
     */
    public Likes getLikesById(int id) {
        return likesRepository.findById(id).orElse(null);
    }

    /**
     * Save dependent analytics.
     * @param likes dependent analytics
     * @return saved dependent analytics
     */
    public Likes saveLikes(Likes likes) {
        return likesRepository.save(likes);
    }
}
