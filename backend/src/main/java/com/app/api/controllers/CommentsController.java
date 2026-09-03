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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for managing comments
 */
@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Operations for managing comments and reactions on bulletin posts")
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
    @Operation(summary = "Get all comments", description = "Retrieves a list of all comments")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved comments")
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
    @Operation(summary = "Get comment by ID", description = "Retrieves a single comment by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment found"),
        @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content)
    })
    public ResponseEntity<Comments> getCommentsById(
        @Parameter(description = "ID of the comment to retrieve", example = "1")
        @PathVariable int id
    ) {
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
    @Operation(summary = "Create a new comment", description = "Creates a new comment on a bulletin post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comment created successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid comment data", content = @Content)
    })
    public ResponseEntity<CommentResponseDTO> createComments(
        @Parameter(description = "ID of the bulletin post", example = "1")
        @PathVariable int postId,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
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
    @Operation(summary = "Update a comment", description = "Updates an existing comment by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
        @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid comment data", content = @Content)
    })
    public ResponseEntity<Comments> updateComments(
        @Parameter(description = "ID of the comment to update", example = "1")
        @PathVariable int id,
        @RequestBody Comments comments
    ) {
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
    @Operation(summary = "Delete a comment from a post", description = "Deletes a comment from a specific bulletin post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comment deleted successfully", content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comment or post not found", content = @Content)
    })
    public ResponseEntity<Void> deleteCommentsUnderPost(
        @Parameter(description = "ID of the bulletin post", example = "1")
        @PathVariable int postId,
        @Parameter(description = "ID of the comment to delete", example = "1")
        @PathVariable int commentId,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
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
    @Operation(summary = "Add helpful reaction to post", description = "Adds a helpful reaction to a bulletin post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reaction added successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Post not found", content = @Content)
    })
    public ResponseEntity<CommentReactionResponseDTO> postHelpfulReation(
        @Parameter(description = "ID of the bulletin post", example = "1")
        @PathVariable int postId,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHead) {

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
    @Operation(summary = "Remove helpful reaction from post", description = "Removes a helpful reaction from a bulletin post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reaction removed successfully", content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Post or reaction not found", content = @Content)
    })
    public ResponseEntity<Void> deleteCommentsUnderPost(
        @Parameter(description = "ID of the bulletin post", example = "1")
        @PathVariable int postId,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ){
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
