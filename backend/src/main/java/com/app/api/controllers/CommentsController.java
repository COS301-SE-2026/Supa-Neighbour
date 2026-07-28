package com.app.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.CommentReactionResponseDTO;
import com.app.api.dtos.CommentRequestDTO;
import com.app.api.dtos.CommentResponseDTO;
import com.app.api.models.Comments;
import com.app.api.services.CommentsService;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.ReactionService;
import com.google.firebase.auth.FirebaseAuthException;
/**
 * REST controller for managing comments
 */
@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    private final ReactionService reactionService;
    private final CommentsService commentsService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Basic Comments constructor
     * @param commentService service for the comments constructor
     */
    public CommentsController(CommentsService commentsService, FirebaseAuthService firebaseAuthService,ReactionService reactionService) {
        this.commentsService = commentsService;
        this.firebaseAuthService = firebaseAuthService;
        this.reactionService=reactionService;

    }

    // GET /api/comments
    /**
     * Retrieves all comments.
     *
     * @return a list of all comments
     */
    @GetMapping
    public ResponseEntity<List<?>> getAllComments() {
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

    // POST /api/comments/bulletin/{postId}
    /**
     * Creates a new comment.
     *
     * @param comments the comment to create
     * @return the created comment with HTTP 201 status
     */
    @PostMapping("/bulletin/{postId}")
    public ResponseEntity<CommentResponseDTO> createComments(
        @PathVariable int postId,
        @RequestHeader("Authorization") String authHeader,
        @RequestBody CommentRequestDTO request) {

        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            CommentResponseDTO created = commentsService.addCommentToPost(postId, request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }catch(FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
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

    // DELETE api/comments/bulletin/posts/{postId}/comments/{commentId}
    /**
     * Deletes a comment under a particular post
     *
     * @param id the ID of the comment to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     * 
     */

    @DeleteMapping("/bulletin/posts/{postId}/{commentId}")
    public ResponseEntity<Void> deleteCommentsUnderPost(
        @PathVariable int postId,
        @PathVariable int commentId,
        @RequestHeader("Authorization") String authHeader
    ) {
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            commentsService.deleteCommentFromPost(postId, commentId, userId);
            return ResponseEntity.noContent().build();
        }catch(FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }  
   
    // PUT /api/bulletin/posta/{1}/like
    /**
     * Updates an existing comment.
     *
     * @param postId the ID of the comment to update
     * @param comments the updated comment data
     * @return the updated comment if found, otherwise 404 Not Found
     */
    @PostMapping("/bulletin/posts/{postId}/like")
    public ResponseEntity<CommentReactionResponseDTO> postHelpfulReation(@PathVariable int postId,@RequestHeader("Authorization") String authHead) {

        try{
            String token = authHead.replace("Bearer ", "");
            int userId= firebaseAuthService.getUserIdFromToken(token);
            CommentReactionResponseDTO created = reactionService.addHelpfulReactionToPost(postId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }catch(FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    
    // DELETE /api/comments/posts/{1}/1
    /**
     * Updates an existing comment.
     *
     * @param postId the ID of the comment needed to be deleted
     * @param request the updated comment data
     * @return the updated comment if found, otherwise 404 Not Found
     */
    @DeleteMapping("/bulletin/posts/{postId}/like")
    public ResponseEntity<Void> deleteCommentsUnderPost(@PathVariable int postId,@RequestHeader("Authorization") String authHeader){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            reactionService.removeHelpfulReaction(postId, userId);
            return ResponseEntity.noContent().build();
        }catch(FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }  
    
}

