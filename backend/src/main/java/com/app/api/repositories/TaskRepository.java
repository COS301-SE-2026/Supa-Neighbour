package com.app.api.repositories;

import com.app.api.models.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Task entities.
 */
@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {

    /**
     * Find all tasks assigned to a specific helper.
     * @param helperId the helper's ID
     * @return tasks assigned to the helper
     */
    Iterable<Task> findByHelperId(int helperId);

    /**
     * Find all tasks linked to a specific dependent.
     * @param dependentId the dependent's ID
     * @return tasks linked to the dependent
     */
    Iterable<Task> findByDependentId(int dependentId);

    /**
     * Find all tasks of a specific task type.
     * @param taskTypeId the task type ID
     * @return tasks matching the given type
     */
    Iterable<Task> findByTaskTypeId(int taskTypeId);
}
