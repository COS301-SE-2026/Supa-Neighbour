package com.app.api.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.api.models.Helper;
import com.app.api.models.TaskInvitation;
import com.app.api.models.TaskInvoice;
import com.app.api.repositories.TaskInvitationRepository;
import com.app.api.repositories.TaskInvoiceRepository;
import com.app.api.repositories.HelperRepository;

import jakarta.transaction.Transactional;


/**
 * Service layer for managing task invitation operations.
 * Provides CRUD functionality for TaskInvitation entities.
 */
@Service
public class TaskInvitationService {

    
    private final TaskInvitationRepository taskInvitationRepository;
    private final TaskInvoiceRepository taskInvoiceRepository;
    private final HelperRepository helperRepository;

    /**
     * Constructs the service with its required repository dependencies.
     *
     * @param taskInvitationRepository repository for {@link TaskInvitation} persistence operations
     * @param taskInvoiceRepository    repository for {@link TaskInvoice} persistence operations
     */
    TaskInvitationService(TaskInvitationRepository taskInvitationRepository, TaskInvoiceRepository taskInvoiceRepository, HelperRepository helperRepository) {
        this.taskInvitationRepository = taskInvitationRepository;
        this.taskInvoiceRepository = taskInvoiceRepository;
        this.helperRepository = helperRepository;
    }

    // Get all
    /**
     * Retrieves all task invitation records from the repository.
     *
     * @return a list of all task invitation records
     */
    public List<TaskInvitation> getTaskInvitations() {
        return taskInvitationRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a task invitation record by its identifier.
     *
     * @param id the task invitation identifier
     * @return the task invitation record if found, or null if no record exists with the given id
     */
    public TaskInvitation getInvitationById(int id) {
        return taskInvitationRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new task invitation record to the repository.
     *
     * @param invitation the task invitation record to save
     * @return the saved task invitation record, or null if the provided task invitation is null
     */
    public TaskInvitation saveTaskInvitation(TaskInvitation invitation) {
        if(invitation == null) {
            return null;
        }
        return taskInvitationRepository.save(invitation);
    }

    // Update
    /**
     * Updates an existing task invitation record with the provided details.
     *
     * @param id      the identifier of the task invitation record to update
     * @param updated the task invitation object containing the updated fields
     * @return the updated task invitation record, or null if no record exists with the given id
     */
    public TaskInvitation updateTaskInvitation(int id, TaskInvitation updated) {
        TaskInvitation existing = taskInvitationRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setInvitedAt(updated.getInvitedAt());
        existing.setHelperId(updated.getHelperId());
        existing.setStatus(updated.getStatus());
        existing.setTaskId(updated.getTaskId());

        return taskInvitationRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a task invitation record by its identifier.
     *
     * @param id the identifier of the task invitation record to delete
     */
    public void deleteTaskInvitation(int id) {
        taskInvitationRepository.deleteById(id);
    }

    /**
     * Invites a helper to a task, handling both first-time invitations and
     * re-invitations of a previously invited/rejected helper.
     * <p>
     * If the helper has an existing invitation that is already "Invited",
     * the call is a no-op and returns {@code null} (already invited).
     * Otherwise, any other pending invitations for the same task that are
     * not already "Invited" or "Rejected" are marked "Rejected", since a
     * task can only have one active invitee at a time. The target helper's
     * invitation is then created or updated to "Invited", and the
     * associated {@link TaskInvoice} is marked "assigned" with the helper set.
     *
     * @param taskId       the ID of the task the helper is being invited to
     * @param helperId     the ID of the helper being invited
     * @param taskInvoice  the task invoice record associated with the task
     * @param helper       the helper being invited
     * @return the created or updated {@link TaskInvitation} with status "Invited",
     *         or {@code null} if the helper was already invited
     */
    @Transactional 
    public TaskInvitation inviteHelper(int taskId, int helperId, TaskInvoice taskInvoice, Helper helper){
        Optional<TaskInvitation> existing = taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(taskId, helperId);

        if(existing.isPresent() && "Invited".equals(existing.get().getStatus())){
            return null;
        }

        List<TaskInvitation> others = taskInvitationRepository.findByTaskId_Taskid(taskId);

        for(TaskInvitation other: others){
            if (other.getHelperId().getHelperid() != helperId
                && !"Invited".equals(other.getStatus())
                && !"Rejected".equals(other.getStatus())) {
            other.setStatus("Rejected");
            taskInvitationRepository.save(other);
            }
        }

        TaskInvitation invitation;
        if(existing.isPresent()){
            invitation = existing.get();
            invitation.setStatus("Invited");
            invitation.setInvitedAt(new Date());
        }else{
            invitation = TaskInvitation.builder().taskId(taskInvoice).helperId(helper).status("Invited").invitedAt(new Date()).build();
        }

        taskInvitationRepository.save(invitation);
        taskInvoice.setStatus("assigned");
        taskInvoice.setHelperid(helper);
        taskInvoiceRepository.save(taskInvoice);
        return invitation;
    }

    /**
     * Records a helper's decline of a task.
     * <p>
     * If an invitation record already exists for this task/helper pair, the
     * decline is rejected: an "Invited" status throws an unprocessable-state
     * exception (the requester has already formally selected the helper, so
     * it's too late to casually decline), and any other existing status
     * throws a conflict exception. Otherwise, a new invitation record is
     * created with status "Declined".
     *
     * @param taskId      the ID of the task being declined
     * @param helperId    the ID of the helper declining the task
     * @param taskInvoice the task invoice record associated with the task
     * @param helper      the helper declining the task
     * @return the newly saved {@link TaskInvitation} with status "Declined"
     * @throws IllegalStateException with message {@code "UNPROCESSABLE"} if the
     *         existing invitation is in "Invited" status, or {@code "CONFLICT"}
     *         if an invitation already exists in any other status
     */
    @Transactional
    public TaskInvitation declineInvitation(int taskId, int helperId, TaskInvoice taskInvoice, Helper helper){
        Optional<TaskInvitation> existing = taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(taskId, helperId);

        if (existing.isPresent()) {
            String status = existing.get().getStatus();
            if ("Invited".equals(status)) {
                throw new IllegalStateException("UNPROCESSABLE"); // requester already selected them — too late to casually decline
            }
            throw new IllegalStateException("CONFLICT"); // already Accepted/Declined/Rejected
        }

        TaskInvitation invitation = TaskInvitation.builder().taskId(taskInvoice).helperId(helper).status("Declined").invitedAt(null).build();

        return taskInvitationRepository.save(invitation);
    }

    /**
     * Records a helper's acceptance of a task.
     * <p>
     * If an invitation record already exists for this task/helper pair, the
     * acceptance is rejected: an "Invited" status throws an unprocessable-state
     * exception, and any other existing status throws a conflict exception.
     * Otherwise, a new invitation record is created with status "Accepted".
     *
     * @param taskId      the ID of the task being accepted
     * @param helperId    the ID of the helper accepting the task
     * @param taskInvoice the task invoice record associated with the task
     * @param helper      the helper accepting the task
     * @return the newly saved {@link TaskInvitation} with status "Accepted"
     * @throws IllegalStateException with message {@code "UNPROCESSABLE"} if the
     *         existing invitation is in "Invited" status, or {@code "CONFLICT"}
     *         if an invitation already exists in any other status
     */
    @Transactional
    public TaskInvitation acceptInvitation(int taskId, int helperId, TaskInvoice taskInvoice, Helper helper){
        TaskInvitation existing = taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(taskId, helperId)
            .orElseThrow(() -> new IllegalStateException("NOT_FOUND"));

        if (existing.getStatus() != null) {
            throw new IllegalStateException("CONFLICT");
        }
        
        existing.setStatus("Accepted");
        TaskInvitation accepted = taskInvitationRepository.save(existing);

        taskInvoice.setStatus("assigned");
        taskInvoice.setHelperid(helper);
        taskInvoiceRepository.save(taskInvoice);
        List<TaskInvitation> others = taskInvitationRepository.findByTaskId_Taskid(taskId);
        for(TaskInvitation other: others){
            if (other.getHelperId().getHelperid() != helperId
                && !"Rejected".equals(other.getStatus())) {
                other.setStatus("Rejected");
                taskInvitationRepository.save(other);
            }
        }

        return accepted;
    }

    /**
     * Retrieves all task invoices for which the specified user has pending task invitations.
     * <p>
     * The method first checks whether the user is registered as a helper. If the user
     * is not a helper, an empty list is returned. Otherwise, all task invitations
     * assigned to the helper with a {@code null} status (pending invitations) are
     * retrieved, and the corresponding task invoices are returned.
     * </p>
     *
     * @param userId the ID of the user whose pending task invoices are to be retrieved
     * @return a list of {@link TaskInvoice} objects associated with the helper's
     *         pending task invitations, or an empty list if the user is not a helper
     */
    @Transactional
    public List<TaskInvoice> getAllTasksBasedOnUserId(int userId){
        Optional<Helper> helperOptional = helperRepository.findByUserid_Userid(userId);

        if(helperOptional.isEmpty()){
            return new ArrayList<>();
        }

        Helper helper = helperOptional.get();

        List<TaskInvitation> pending = taskInvitationRepository.findByHelperId_HelperidAndStatus(helper.getHelperid(), null);
        List<Integer> taskIds = new ArrayList<>();
        for(TaskInvitation invitation: pending){
            TaskInvoice taskInvoice = invitation.getTaskId();
            if(taskInvoice != null){
                taskIds.add(taskInvoice.getTaskid());
            }
        }
        return taskInvoiceRepository.findAllById(taskIds);
    }
}
