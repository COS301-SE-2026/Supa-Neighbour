package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.TaskImage;

/**
 * Repository for TaskImage entities.
 */
@Repository
public interface TaskImageRepository extends JpaRepository<TaskImage, Integer> {
}
