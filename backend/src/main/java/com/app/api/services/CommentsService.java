package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Comments;
import com.app.api.repositories.CommentsRepository;

@Service
public class CommentsService {

    @Autowired
    private CommentsRepository commentsRepository;

    // Get all
    public List<Comments> getAllComments() {
        return commentsRepository.findAll();
    }

    // Get by id
    public Comments getCommentsById(int id) {
        return commentsRepository.findById(id).orElse(null);
    }

    // Create
    public Comments saveComments(Comments comments) {
        if(comments == null) return null;
        return commentsRepository.save(comments);
    }

    // Update
    public Comments updateComments(int id, Comments updated) {
        Comments existing = commentsRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setUserid(updated.getUserid());
        existing.setUpdatedAt(updated.getUpdatedAt());
        existing.setCommentContent(updated.getCommentContent());
        existing.setParentCommentid(updated.getParentCommentid());
        existing.setCreatedAt(updated.getCreatedAt());
        existing.setPostid(updated.getPostid());
        existing.setUpdatedAt(updated.getUpdatedAt());
        return commentsRepository.save(existing);
    }

    // Delete
    public void deleteComments(int id) {
        commentsRepository.deleteById(id);
    }
}