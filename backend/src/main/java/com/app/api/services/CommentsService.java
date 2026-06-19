package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Comments;
import com.app.api.repositories.CommentsRepository;

/**
 * Service layer for managing comment operations.
 * Provides CRUD functionality for Comments entities.
 */
@Service
public class CommentsService {

    @Autowired
    private CommentsRepository commentsRepository;

    /**
     * Retrieves all comments from the repository.
     *
     * @return a list of all comments
     */
    public List<Comments> getAllComments() {
        return commentsRepository.findAll();
    }

    /**
     * Retrieves a comment by its identifier.
     *
     * @param id the comment identifier
     * @return the comment if found, or null if no comment exists with the given id
     */
    public Comments getCommentsById(int id) {
        return commentsRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new comment to the repository.
     *
     * @param comments the comment to save
     * @return the saved comment, or null if the provided comment is null
     */
    public Comments saveComments(Comments comments) {
        if(comments == null) {
            return null;
        }
        return commentsRepository.save(comments);
    }

    /**
     * Updates an existing comment with the provided details.
     *
     * @param id      the identifier of the comment to update
     * @param updated the comment object containing the updated fields
     * @return the updated comment, or null if no comment exists with the given id
     */
    public Comments updateComments(int id, Comments updated) {
        Comments existing = commentsRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setUserid(updated.getUserid());
        existing.setUpdatedAt(updated.getUpdatedAt());
        existing.setCommentContent(updated.getCommentContent());
        existing.setParentCommentid(updated.getParentCommentid());
        existing.setCreatedAt(updated.getCreatedAt());
        existing.setPostid(updated.getPostid());
        existing.setUpdatedAt(updated.getUpdatedAt());
        return commentsRepository.save(existing);
    }

    /**
     * Deletes a comment by its identifier.
     *
     * @param id the identifier of the comment to delete
     */
    public void deleteComments(int id) {
        commentsRepository.deleteById(id);
    }
}
