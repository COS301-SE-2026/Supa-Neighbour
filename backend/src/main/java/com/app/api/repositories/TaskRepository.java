package com.app.api.repositories;

import com.app.api.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Task entities.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    /**
     * Find all tasks assigned to a specific helper.
     * @param helperId the helper's ID
     * @return tasks assigned to the helper
     */
    List<Task> findByHelperId(int helperId);

    /**
     * Find all tasks linked to a specific dependent.
     * @param dependentId the dependent's ID
     * @return tasks linked to the dependent
     */
    List<Task> findByDependentId(int dependentId);

    /**
     * Find all tasks of a specific task type.
     * @param taskTypeId the task type ID
     * @return tasks matching the given type
     */
    List<Task> findByTaskTypeId(int taskTypeId);
}
