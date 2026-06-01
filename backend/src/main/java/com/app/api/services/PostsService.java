package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Posts;
import com.app.api.repositories.PostsRepository;

@Service
public class PostsService {

    @Autowired
    private PostsRepository postsRepository;

    // Get all
    public List<Posts> getAllPosts() {
        return postsRepository.findAll();
    }

    // Get by id
    public Posts getPostById(int id) {
        return postsRepository.findById(id).orElse(null);
    }

    // Create
    public Posts savePost(Posts post) {
        if(post == null) return null;
        return postsRepository.save(post);
    }

    // Update
    public Posts updatePost(int id, Posts updated) {
        Posts existing = postsRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setUserid(updated.getUserid());
        existing.setPostContent(updated.getPostContent());
        existing.setCreatedAt(updated.getCreatedAt());
        existing.setUpdatedAt(updated.getUpdatedAt());
        existing.setMediaURL(updated.getMediaURL());


        return postsRepository.save(existing);
    }

    // Delete
    public void deletePost(int id) {
        postsRepository.deleteById(id);
    }
}