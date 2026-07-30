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
import com.app.api.dtos.ReactionRemovedResponseDTO;
import com.app.api.dtos.ReactionResponseDTO;
import com.app.api.models.Reaction;
import com.app.api.services.ReactionService;


import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;

/**
 * REST controller for reaction.
 */
@RestController
@RequestMapping("/api/reaction")
public class ReactionController {


    private final ReactionService reactionService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Basic reaction Contructor
     * 
     * @param ReactionService reactionService
     */
    public ReactionController(ReactionService reactionService, FirebaseAuthService firebaseAuthService) {
        this.reactionService = reactionService;
        this.firebaseAuthService = firebaseAuthService;
    }

    // GET /api/reaction
    /**
     * Retrieves all reaction.
     *
     * @return a list of all reaction
     */
    @GetMapping
    public ResponseEntity<List<Reaction>> getAllreaction() {
        return ResponseEntity.ok(reactionService.getAllreaction());
    }

    // GET /api/reaction/1
    /**
     * Retrieves a like by its ID.
     *
     * @param id the like ID
     * @return the reaction if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reaction> getreactionById(@PathVariable int id) {
        Reaction reaction = reactionService.getLikeById(id);
        if (reaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reaction);
    }

    // POST /api/reaction/posts/{postId}/dislike
    /**
     * Creates a new like.
     *
     * @param reaction the comment to create
     * @return the created reaction with HTTP 201 status
     */
    @PostMapping("/posts/{postId}/dislike")
    public ResponseEntity<ReactionResponseDTO> createreaction(
        @PathVariable int postId,
        @RequestHeader("Authorization") String authHeader
    ) {

        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            ReactionResponseDTO created = reactionService.addDislikeReaction(postId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }catch(FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // PUT /api/reaction/1
    /**
     * Updates an existing reaction.
     *
     * @param id the ID of the reaction to update
     * @param reaction the updated reaction data
     * @return the updated reaction if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reaction> updatereaction(@PathVariable int id, @RequestBody Reaction reaction) {
        Reaction existing = reactionService.getLikeById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Reaction updated = reactionService.updateLike(id, reaction);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/reaction/posts/{postId}/dislike
    /**
     * Deletes a reaction to a post
     *
     * @param id the ID of the reaction to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/posts/{postId}/dislike")
    public ResponseEntity<ReactionRemovedResponseDTO> removeDislike(
        @PathVariable int postId,
        @RequestHeader("Authorization") String authHeader
    ) {
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            ReactionRemovedResponseDTO result = reactionService.removeDisLikeReaction(postId, userId);
            return ResponseEntity.ok(result);
        }catch(FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    //get dislikes
    /**
    * Creates a new comment for a post.
    *
    * @param commentId to add a dislike to a post.
    * @return successful http status 201.
    */
    @PostMapping("/comments/{commentId}/dislike")
    public ResponseEntity<CommentReactionResponseDTO>addDislikedToComment(
            @PathVariable int commentId,
            @RequestHeader("Authorization") String authHeader) {

        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            CommentReactionResponseDTO created = reactionService.addDislikeReactionToComment(commentId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }catch(FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    
    
}
