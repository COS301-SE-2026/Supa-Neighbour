package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.app.api.models.TaskInvitation;
import java.util.Optional;
import java.util.List;

/**
 * Repository for TaskInvitations entities
 */
@Repository
public interface TaskInvitationRepository extends JpaRepository<TaskInvitation, Integer> {

    Optional<TaskInvitation>findByTaskId_TaskidAndHelperId_Helperid(int taskId, int helperId);

    List<TaskInvitation>findByTaskId_Taskid(int taskId);
}