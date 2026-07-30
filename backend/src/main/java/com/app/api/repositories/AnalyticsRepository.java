package com.app.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.Analytics;

/**
 * Repository for analytics entities
 */
@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Integer> {

    /**
     * Finds all Analytics records associated with a specific task.
     *
     * @param taskId the ID of the task
     * @return list of Analytics entities for the given task
     */
    List<Analytics> findByTaskid_Taskid(int taskId);
}
