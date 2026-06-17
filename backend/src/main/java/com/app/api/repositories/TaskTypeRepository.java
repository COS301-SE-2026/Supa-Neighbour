package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.TaskType;
@Repository
public interface TaskTypeRepository extends JpaRepository<TaskType, Integer> {
    
}
