package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.models.Comments;
import com.app.api.services.CommentsService;

/**
 * REST controller for managing comments
 */
@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    // GET /api/comments
    /**
     * Retrieves all comments.
     *
     * @return a list of all comments
     */
    @GetMapping
    public ResponseEntity<List<Comments>> getAllComments() {
        return ResponseEntity.ok(commentsService.getAllComments());
    }

    // GET /api/comments/1
    /**
     * Retrieves a comment by its ID.
     *
     * @param id the comment ID
     * @return the comment if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Comments> getCommentsById(@PathVariable int id) {
        Comments comments = commentsService.getCommentsById(id);
        if (comments == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comments);
    }

    // POST /api/comments
    /**
     * Creates a new comment.
     *
     * @param comments the comment to create
     * @return the created comment with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Comments> createComments(@RequestBody Comments comments) {
        Comments saved = commentsService.saveComments(comments);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/comments/1
    /**
     * Updates an existing comment.
     *
     * @param id the ID of the comment to update
     * @param comments the updated comment data
     * @return the updated comment if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Comments> updateComments(@PathVariable int id, @RequestBody Comments comments) {
        Comments existing = commentsService.getCommentsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Comments updated = commentsService.updateComments(id, comments);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/comments/1
    /**
     * Deletes a comment by its ID.
     *
     * @param id the ID of the comment to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComments(@PathVariable int id) {
        Comments existing = commentsService.getCommentsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        commentsService.deleteComments(id);
        return ResponseEntity.noContent().build();
    }
}
