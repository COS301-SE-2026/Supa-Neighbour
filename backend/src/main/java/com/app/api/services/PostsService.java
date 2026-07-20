package com.app.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.api.models.Posts;
import com.app.api.repositories.PostsRepository;

/**
 * Service layer for managing post operations.
 * Provides CRUD functionality for Posts entities.
 */
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    /**
     * Constructs the repository with its required service dependency.
     *
     * @param postsRepository repository providing analytics data for posts
     */
    public PostsService(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    // Get all
    /**
     * Retrieves all posts from the repository.
     *
     * @return a list of all posts
     */
    public List<Posts> getAllPosts() {
        return postsRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a post by its identifier.
     *
     * @param id the post identifier
     * @return the post if found, or null if no post exists with the given id
     */
    public Posts getPostById(int id) {
        return postsRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new post to the repository.
     *
     * @param post the post to save
     * @return the saved post, or null if the provided post is null
     */
    public Posts savePost(Posts post) {
        if(post == null) {
            return null;
        }
        return postsRepository.save(post);
    }

    // Update
    /**
     * Updates an existing post with the provided details.
     *
     * @param id      the identifier of the post to update
     * @param updated the post object containing the updated fields
     * @return the updated post, or null if no post exists with the given id
     */
    public Posts updatePost(int id, Posts updated) {
        Posts existing = postsRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setUserid(updated.getUserid());
        existing.setPostContent(updated.getPostContent());
        existing.setCreatedAt(updated.getCreatedAt());
        existing.setUpdatedAt(updated.getUpdatedAt());
        existing.setMediaURL(updated.getMediaURL());


        return postsRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a post by its identifier.
     *
     * @param id the identifier of the post to delete
     */
    public void deletePost(int id) {
        postsRepository.deleteById(id);
    }
}
