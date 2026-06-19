package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Comments;

public interface CommentsRepository extends JpaRepository<Comments,Integer>{
    
}
