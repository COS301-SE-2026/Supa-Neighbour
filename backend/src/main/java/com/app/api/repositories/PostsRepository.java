package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Posts;

public interface PostsRepository extends JpaRepository<Posts,Integer> {
    
}
