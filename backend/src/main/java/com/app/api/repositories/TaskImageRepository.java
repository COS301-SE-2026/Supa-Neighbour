package com.app.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.TaskImage;

/**
 * Repository for TaskImage entities.
 */
@Repository
public interface TaskImageRepository extends JpaRepository<TaskImage, Integer> {
     /**
     * Finds images for a task, ordered by upload time ascending.
     *
     * @param taskId the task ID
     * @return matching images, or empty list if none
     */
    List<TaskImage> findByTaskid_TaskidOrderByUploadedAtAsc(int taskId);
    /**
     * Finds images for multiple tasks.
     *
     * @param taskIds the task IDs
     * @return matching images, or empty list if none
     */
    List<TaskImage> findByTaskid_TaskidIn(List<Integer> taskIds);
}
