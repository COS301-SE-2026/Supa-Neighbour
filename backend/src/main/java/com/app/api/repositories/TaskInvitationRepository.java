package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.app.api.models.TaskInvitation;

/**
 * Repository for TaskInvitations entities
 */
@Repository
public interface TaskInvitationRepository extends JpaRepository<TaskInvitation, Integer> {
}
