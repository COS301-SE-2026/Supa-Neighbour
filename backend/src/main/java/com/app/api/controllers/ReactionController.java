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
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.ReactionService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for reaction.
 */
@RestController
@RequestMapping("/api/reaction")
@Tag(name = "Reactions", description = "Operations for managing post and comment reactions (likes/dislikes)")
public class ReactionController {

    private final ReactionService reactionService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Basic reaction Constructor
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
    @Operation(summary = "Get all reactions", description = "Retrieves a list of all reactions")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reactions")
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
    @Operation(summary = "Get reaction by ID", description = "Retrieves a single reaction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reaction found"),
        @ApiResponse(responseCode = "404", description = "Reaction not found", content = @Content)
    })
    public ResponseEntity<Reaction> getreactionById(
        @Parameter(description = "ID of the reaction to retrieve", example = "1")
        @PathVariable int id
    ) {
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
    @Operation(
        summary = "Add dislike to post",
        description = "Adds a dislike reaction to a post by the authenticated user",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dislike added successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Post not found", content = @Content)
    })
    public ResponseEntity<ReactionResponseDTO> createreaction(
        @Parameter(description = "ID of the post to dislike", example = "1")
        @PathVariable int postId,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
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
    @Operation(
        summary = "Update a reaction",
        description = "Updates an existing reaction by its ID",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reaction updated successfully"),
        @ApiResponse(responseCode = "404", description = "Reaction not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid reaction data", content = @Content)
    })
    public ResponseEntity<Reaction> updatereaction(
        @Parameter(description = "ID of the reaction to update", example = "1")
        @PathVariable int id, 
        @RequestBody Reaction reaction
    ) {
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
    @Operation(
        summary = "Remove dislike from post",
        description = "Removes a dislike reaction from a post by the authenticated user",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dislike removed successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Post or reaction not found", content = @Content)
    })
    public ResponseEntity<ReactionRemovedResponseDTO> removeDislike(
        @Parameter(description = "ID of the post to remove dislike from", example = "1")
        @PathVariable int postId,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
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
    @Operation(
        summary = "Add dislike to comment",
        description = "Adds a dislike reaction to a comment by the authenticated user",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dislike added to comment successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content)
    })
    public ResponseEntity<CommentReactionResponseDTO> addDislikedToComment(
        @Parameter(description = "ID of the comment to dislike", example = "1")
        @PathVariable int commentId,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ) {

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
