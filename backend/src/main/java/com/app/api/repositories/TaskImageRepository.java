package com.app.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * True if any stored image already has this SHA-256 (exact re-upload of the same file).
     * Check this BEFORE saving: ux_task_image_hash would reject a duplicate insert with an exception.
     */
    boolean existsByImageHash(String imageHash);

    /**
     * The task's earliest image of the given type, e.g. its REFERENCE photo.
     *
     * @param taskId    the task ID
     * @param imageType "REFERENCE" or "COMPLETION"
     */
    Optional<TaskImage> findFirstByTaskid_TaskidAndImageTypeOrderByUploadedAtAsc(int taskId, String imageType);
    
    /**
     * Perceptual hashes of every COMPLETION photo belonging to OTHER tasks, for near-duplicate checks.
     *
     * @param taskId the task being verified (excluded)
     */
    @Query(
        "select i.perceptualHash from TaskImage i "
            + "where i.imageType = 'COMPLETION' and i.perceptualHash is not null and i.taskid.taskid <> :taskId"
    )
    List<Long> findCompletionPerceptualHashesExcludingTask(@Param("taskId") int taskId); 

    /**
     * Returns all TaskImages whose task's ID is in {@code taskIds} and whose
     * image type equals {@code imageType}.
     *
     * @param taskIds   task IDs to filter by; empty list yields no results
     * @param imageType image type to filter by
     * @return matching TaskImages; never null, possibly empty
     */
    List<TaskImage> findByTaskid_TaskidInAndImageType(List<Integer> taskIds, String imageType);
}
