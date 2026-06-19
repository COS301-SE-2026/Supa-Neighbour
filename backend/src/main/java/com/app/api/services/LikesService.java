package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Likes;
import com.app.api.repositories.LikesRepository;

/**
 * Service layer for managing like operations.
 * Provides CRUD functionality for Likes entities.
 */
@Service
public class LikesService {

    @Autowired
    private LikesRepository likesRepository;

    /**
     * Retrieves all likes from the repository.
     *
     * @return a list of all likes
     */
    public List<Likes> getAllLikes() {
        return likesRepository.findAll();
    }

    /**
     * Retrieves a like by its identifier.
     *
     * @param id the like identifier
     * @return the like if found, or null if no like exists with the given id
     */
    public Likes getLikeById(int id) {
        return likesRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new like to the repository.
     *
     * @param like the like to save
     * @return the saved like, or null if the provided like is null
     */
    public Likes saveLike(Likes like) {
        if(like == null) {
            return null;
        }
        return likesRepository.save(like);
    }

    /**
     * Updates an existing like with the provided details.
     *
     * @param id      the identifier of the like to update
     * @param updated the like object containing the updated fields
     * @return the updated like, or null if no like exists with the given id
     */
    public Likes updateLike(int id, Likes updated) {
        Likes existing = likesRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setCommentid(updated.getCommentid());
        existing.setPostid(updated.getPostid());
        existing.setUserid(updated.getUserid());
        existing.setCreatedAt(updated.getCreatedAt());
        existing.setUpdatedAt(updated.getUpdatedAt());

        return likesRepository.save(existing);
    }

    /**
     * Deletes a like by its identifier.
     *
     * @param id the identifier of the like to delete
     */
    public void deleteLike(int id) {
        likesRepository.deleteById(id);
    }
}
