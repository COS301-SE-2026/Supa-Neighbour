package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Posts;
import com.app.api.services.PostsService;

@RestController
@RequestMapping("/api/posts")
public class PostsController {

    @Autowired
    private PostsService postsService;

    // GET /api/posts
    @GetMapping
    public ResponseEntity<List<Posts>> getAllPosts() {
        return ResponseEntity.ok(postsService.getAllPosts());
    }

    // GET /api/posts/1
    @GetMapping("/{id}")
    public ResponseEntity<Posts> getPostsById(@PathVariable int id) {
        Posts posts = postsService.getPostById(id);
        if (posts == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(posts);
    }

    // POST /api/posts
    @PostMapping
    public ResponseEntity<Posts> createPosts(@RequestBody Posts posts) {
        Posts saved = postsService.savePost(posts);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/posts/1
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