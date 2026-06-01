package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Likes;
import com.app.api.repositories.LikesRepository;

@Service
public class LikesService {

    @Autowired
    private LikesRepository likesRepository;

    // Get all
    public List<Likes> getAllLikes() {
        return likesRepository.findAll();
    }

    // Get by id
    public Likes getLikeById(int id) {
        return likesRepository.findById(id).orElse(null);
    }

    // Create
    public Likes saveLike(Likes like) {
        return likesRepository.save(like);
    }

    // Update
    public Likes updateLike(int id, Likes updated) {
        Likes existing = likesRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setCommentid(updated.getCommentid());
        existing.setPostid(updated.getPostid());
        existing.setUserid(updated.getUserid());
        existing.setCreatedAt(updated.getCreatedAt());
        existing.setUpdatedAt(updated.getUpdatedAt());

        return likesRepository.save(existing);
    }

    // Delete
    public void deleteLike(int id) {
        likesRepository.deleteById(id);
    }
}