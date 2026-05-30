package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Comments;
import com.app.api.repositories.CommentsRepository;

/**
 * Dependent analytics service.
 */
@Service
public class CommentsService {

    @Autowired
    private CommentsRepository commentsRepository;

    /**
     * Get all dependent analytics.
     * @return list of dependent analytics
     */
    public List<Comments> getAllComments() {
        return commentsRepository.findAll();
    }

    /**
     * Get dependent analytics by id.
     * @param id dependent analytics id
     * @return dependent analytics
     */
    public Comments getCommentsById(int id) {
        return commentsRepository.findById(id).orElse(null);
    }

    /**
     * Save dependent analytics.
     * @param dependentAnalytics dependent analytics
     * @return saved dependent analytics
     */
    public Comments saveComments(Comments comments) {
        return commentsRepository.save(comments);
    }
}