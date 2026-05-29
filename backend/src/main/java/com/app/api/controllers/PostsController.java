package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Posts;
import com.app.api.services.PostsService;

/**
 * Address controller.
 */
@RestController
@RequestMapping("api/posts")
public class PostsController {

    @Autowired
    private PostsService postsService;

    /**
     * Get all addresses.
     * @return addresses
     */
    @GetMapping
    public Iterable<Posts> getAllPosts() {
        return postsService.getAllPosts();
    }

    /**
     * Get address by id.
     * @param id address id
     * @return address
     */
    @GetMapping("api/posts/{id}")
    public Posts getPostsById(@PathVariable int id) {
        return postsService.getPostsById(id);
    }

    /**
     * Create address.
     * @param posts address
     * @return saved address
     */
    @PostMapping
    public Posts createPosts(@RequestBody Posts posts) {
        return postsService.savePosts(posts);
    }
}