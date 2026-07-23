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

    /**
     * Finds the invitation record for a specific task/helper pair.
     *
     * @param taskId   the ID of the task
     * @param helperId the ID of the helper
     * @return an {@link Optional} containing the matching invitation, or empty if none exists
     */
    Optional<TaskInvitation>findByTaskId_TaskidAndHelperId_Helperid(int taskId, int helperId);

    /**
     * Finds all invitation records associated with a given task.
     *
     * @param taskId the ID of the task
     * @return a list of invitations for the task, possibly empty
     */
    List<TaskInvitation>findByTaskId_Taskid(int taskId);
    
}
