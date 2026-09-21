package com.app.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.TaskVerification;

/**
 * Repository for TaskVerification entities.
 */
@Repository
public interface TaskVerificationRepository extends JpaRepository<TaskVerification, Integer> {

    /**
     * Finds the most recent verification for a task (each submitted photo gets its own row,
     * so the newest one is the task's current result).
     *
     * @param taskId the task ID
     * @return the newest verification, or empty if none exists
     */
    Optional<TaskVerification> findFirstByTask_TaskIdOrderByCreatedAtDesc(int taskId);
}