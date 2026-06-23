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

import com.app.api.models.Posts;
import com.app.api.services.PostsService;

/**
 * REST controller for Posts.
 */
@RestController
@RequestMapping("/api/posts")
public class PostsController {

    @Autowired
    private PostsService postsService;

    // GET /api/posts
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

    // POST /api/posts
    /**
     * Creates a new posts.
     *
     * @param location the posts to create
     * @return the created posts with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Posts> createPosts(@RequestBody Posts posts) {
        Posts saved = postsService.savePost(posts);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
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
