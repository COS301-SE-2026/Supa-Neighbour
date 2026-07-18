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
import org.springframework.web.bind.annotation.RestController;
 
import com.app.api.dtos.CreatePostRequest;
import com.app.api.models.Posts;
import com.app.api.models.User;
import com.app.api.repositories.UserRepository;
import com.app.api.services.PostsService;
import com.app.api.services.PostsService.InvalidPostException;
import com.app.api.services.FirebaseAuthService;
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

    // GET /api/bulletin
    /**
     * Retrieves all posts.
     *
     * @return a list of all posts
     */
    @GetMapping
    public ResponseEntity<List<Posts>> getAllPosts() {
        return ResponseEntity.ok(postsService.getAllPosts());
    }

    // GET /api/posts/1
     /**
     * Retrieves a posts by its ID.
     *
     * @param id the posts ID
     * @return the posts if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Posts> getPostsById(@PathVariable int id) {
        Posts posts = postsService.getPostById(id);
        if (posts == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(posts);
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

    // PUT /api/posts/1
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

    // DELETE /api/posts/1
    /**
     * Deletes a posts by its ID.
     *
     * @param id the ID of the posts to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosts(@PathVariable int id) {
        Posts existing = postsService.getPostById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        postsService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
