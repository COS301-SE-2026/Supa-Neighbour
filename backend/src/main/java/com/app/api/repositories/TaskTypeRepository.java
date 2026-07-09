package com.app.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.TaskType;

/**
 * Repository for TaskType entities.
 */
@Repository
public interface TaskTypeRepository extends JpaRepository<TaskType, Integer> {
    /**
     * Retrieves all task types whose descriptions match one of the
     * supplied descriptions.
     *
     * @param descriptions the list of task type descriptions to search for
     * @return a list of matching {@link TaskType} entities
     */
    List<TaskType> findByDescriptionIn(List<String> descriptions);
}
