package com.app.api.controllers;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.CommentPostResponseDTO;
import com.app.api.dtos.CreatePostRequest;
import com.app.api.dtos.PostDetailDTO;
import com.app.api.dtos.PostFeedResponseDTO;
import com.app.api.models.Posts;
import com.app.api.services.CommentsService;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.PostsService;
import com.app.api.services.PostsService.InvalidPostException;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for Posts.
 */
@RestController
@RequestMapping("/api/bulletin")
@Tag(name = "Bulletin Posts", description = "Operations for managing bulletin board posts and comments")
public class PostsController {

    private final PostsService postsService;
    private final FirebaseAuthService firebaseAuthService;
    private final CommentsService commentsService;

    /**
     * Constructs the controller with its required service dependency.
     *
     * @param postsService service providing analytics data for dependents
     */
    public PostsController(PostsService postsService,FirebaseAuthService firebaseAuthService, CommentsService commentsService) {
        this.postsService = postsService;
        this.firebaseAuthService = firebaseAuthService;
        this.commentsService = commentsService;
    }

    // GET /api/bulletin/posts
    /**
     * Returns the bulletin board feed for the caller's neighbourhood zone (7.1),
     * with optional category filter, keyword search, and pagination.
     *
     * @param category   optional category filter
     * @param search     optional keyword search against post content
     * @param page       page number, default 1
     * @param limit      posts per page, default 20
     * @param authHeader the Authorization header, expected as "Bearer <token>"
     * @return the feed response, or the appropriate error status
     */
    @GetMapping("/posts")
    @Operation(
        summary = "Get bulletin feed",
        description = "Returns the bulletin board feed for the caller's neighbourhood zone with optional filtering and pagination",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Feed retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content)
    })
    public ResponseEntity<?> getAllPosts(
        @Parameter(description = "Optional category filter", example = "General")
        @RequestParam(required = false) String category,
        @Parameter(description = "Optional keyword search against post content", example = "help")
        @RequestParam(required = false) String search,
        @Parameter(description = "Page number for pagination", example = "1")
        @RequestParam(required = false) Integer page,
        @Parameter(description = "Number of posts per page", example = "20")
        @RequestParam(required = false) Integer limit,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ) {
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            PostFeedResponseDTO feed = postsService.getFeed(userId, category, search, page, limit);
            return ResponseEntity.ok(feed);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // GET /api/posts/{postId}
    /**
     * Returns a single post's detail view (7.2), including its full comments
     * list rather than just a count.
     *
     * @param postId     the post ID
     * @param authHeader the Authorization header, expected as "Bearer <token>"
     * @return the post detail if found, otherwise 404, or 401 if unauthenticated
     */
    @GetMapping("/posts/{postId}")
    @Operation(
        summary = "Get post details",
        description = "Returns a single post's detail view including its full comments list",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post details retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Post not found", content = @Content)
    })
    public ResponseEntity<?> getPostsById(
        @Parameter(description = "ID of the post to retrieve", example = "1")
        @PathVariable int postId, 
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
        ) {
       try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            PostDetailDTO detail = postsService.getPostDetail(postId);
            if(detail == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Post not found"));
            }
            return ResponseEntity.ok(detail);
       }catch(FirebaseAuthException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
       }
    }

   // POST /api/bulletin/posts
    /**
     *
     * Creates a post by an authenticated user
     * 
     * @param request the create-post request body
     * @return the created post with HTTP 201, or the appropriate error status
     */
    @PostMapping("/posts")
    @Operation(
        summary = "Create a new post",
        description = "Creates a new bulletin post by an authenticated user",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Post created successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid post data", content = @Content)
    })
    public ResponseEntity<?> createPosts(
        @RequestBody CreatePostRequest request,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ) {
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            Posts saved = postsService.createPost(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }catch(FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }catch(InvalidPostException e){
            return ResponseEntity
                .status(HttpStatus.valueOf(e.getStatusCode()))
                .header("Content-Type", "application/json")
                .body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/bulletin/{postId}
    /**
     * Updates an existing posts.
     *
     * @param id the ID of the posts to update
     * @param likes the updated posts data
     * @return the updated posts if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a post",
        description = "Updates an existing post by its ID",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post updated successfully"),
        @ApiResponse(responseCode = "404", description = "Post not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid post data", content = @Content)
    })
    public ResponseEntity<Posts> updatePosts(
        @Parameter(description = "ID of the post to update", example = "1")
        @PathVariable int id, 
        @RequestBody Posts posts
    ) {
        Posts existing = postsService.getPostById(id);
        if (existing == null){
            return ResponseEntity.notFound().build();
        }
        Posts updated = postsService.updatePost(id, posts);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/bulletin/posts
    /**
     * Deletes a posts by its ID.
     *
     * @param id the ID of the posts to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/posts/{postId}")
    @Operation(
        summary = "Delete a post",
        description = "Deletes a post by its ID if the authenticated user is the owner",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "403", description = "Not authorized to delete this post", content = @Content),
        @ApiResponse(responseCode = "404", description = "Post not found", content = @Content)
    })
    public ResponseEntity<?> deletePosts(
        @Parameter(description = "ID of the post to delete", example = "1")
        @PathVariable int postId,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
        ) {
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            postsService.deletePost(postId, userId);
            return ResponseEntity.ok(Map.of("message", "Post deleted", "postId", postId)); 
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }catch(InvalidPostException e){
            return ResponseEntity
                .status(HttpStatus.valueOf(e.getStatusCode()))
                .header("Content-Type", "application/json")
                .body(Map.of("error", e.getMessage()));
        }
    }

    //Get api bulletin/posts
    /**
    * returns all comments to a post.
     *
    * @param postId The comment request.
    * @return comments and 200 status.
    */
    @GetMapping("/posts/{postId}/comments")
    @Operation(
        summary = "Get comments for a post",
        description = "Returns all comments for a specific post",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Post not found", content = @Content)
    })
    public ResponseEntity<?> getPostComments(
        @Parameter(description = "ID of the post to get comments for", example = "1")
        @PathVariable int postId, 
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ) {
        try{
            String token = authHeader.replace("Bearer ","");
            int userId= firebaseAuthService.getUserIdFromToken(token);
            List<CommentPostResponseDTO> comments = commentsService.getCommentsByPostId(postId,userId);
            return ResponseEntity.ok(comments);

        }catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    
}
