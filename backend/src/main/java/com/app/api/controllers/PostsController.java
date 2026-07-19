package com.app.api.controllers;
 
import java.util.List;
import java.util.Map;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
 
import com.app.api.dtos.CreatePostRequest;
import com.app.api.dtos.PostDetailDTO;
import com.app.api.dtos.PostFeedResponseDTO;
import com.app.api.models.Posts;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.PostsService;
import com.app.api.services.PostsService.InvalidPostException;
import com.google.firebase.auth.FirebaseAuthException;

/**
 * REST controller for Posts.
 */
@RestController
@RequestMapping("/api/bulletin")
public class PostsController {

    
    private final PostsService postsService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs the controller with its required service dependency.
     *
     * @param postsService service providing analytics data for dependents
     */
    public PostsController(PostsService postsService,FirebaseAuthService firebaseAuthService) {
        this.postsService = postsService;
        this.firebaseAuthService = firebaseAuthService;
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
    public ResponseEntity<?> getAllPosts(
        @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
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
    public ResponseEntity<?> getPostsById(
        @PathVariable int postId, 
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
    public ResponseEntity<?> createPosts(
        @RequestBody CreatePostRequest request,
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
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getMessage()));
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
    public ResponseEntity<Posts> updatePosts(@PathVariable int id, @RequestBody Posts posts) {
        Posts existing = postsService.getPostById(id);
        if (existing == null) {
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
    public ResponseEntity<?> deletePosts(
        @PathVariable int postId,
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
