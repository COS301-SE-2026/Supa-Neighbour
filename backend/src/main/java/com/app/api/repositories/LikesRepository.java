package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Likes;

public interface LikesRepository extends JpaRepository<Likes,Integer> {
    
}
