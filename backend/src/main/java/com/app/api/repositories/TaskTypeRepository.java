package com.app.api.repositories;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.TaskType;

/**
 * Repository for TaskType entities.
 */
@Repository
public interface TaskTypeRepository extends JpaRepository<TaskType, Integer> {
    List<TaskType> findByDescriptionIn(List<String> descriptions);
}
