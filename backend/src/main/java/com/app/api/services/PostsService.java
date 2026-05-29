package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Posts;
import com.app.api.repositories.PostsRepository;

/**
 * Dependent analytics service.
 */
@Service
public class PostsService {

    private final PostsRepository postsRepository;
    @Autowired
    private PostsService postsService;

    PostsService(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    /**
     * Get all dependent analytics.
     * @return list of dependent analytics
     */
    public Iterable<Posts> getAllPosts() {
        return postsRepository.findAll();
    }

    /**
     * Get dependent analytics by id.
     * @param id dependent analytics id
     * @return dependent analytics
     */
    public Posts getPostsById(int id) {
        return postsRepository.findById(id).orElse(null);
    }

    /**
     * Save dependent analytics.
     * @param posts dependent analytics
     * @return saved dependent analytics
     */
    public Posts savePosts(Posts posts) {
        return postsRepository.save(posts);
    }
}
