package com.app.api.repositories;

import com.app.api.models.Analytics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Analytics entities.
 */
@Repository
public interface AnalyticsRepository extends CrudRepository<Analytics, Integer> {

    /**
     * Find all analytics records linked to a specific task.
     * @param taskId the task ID
     * @return analytics records associated with the task
     */
    Iterable<Analytics> findByTaskId(int taskId);
}
