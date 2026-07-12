package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.TaskInvitation;

/**
 * Repository for TaskInvitations entities
 */
public interface TaskInvitationRepository extends JpaRepository<TaskInvitation, Integer> {
}
