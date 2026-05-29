package com.app.api.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.app.api.models.Comments;

public interface CommentsRepository extends CrudRepository<Comments,Integer>{
    
}