package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import java.util.Date;
/**
 * Represents an invitation extended to a helper for a specific task.
 * <p>
 * This entity maps to the {@code task_invitation_table} database table and
 * tracks the pending/accepted/declined lifecycle of an invite, sitting
 * upstream of {@link TaskInvoice}. Note that status values here use a
 * capitalized convention ("Invited", "Accepted", "Declined", "Rejected"),
 * unlike the lowercase convention used on {@code task_invoice_table}.
 * </p>
 */
@Data
@Builder
@Entity
@Table(name = "task_invitation_table")
public class TaskInvitation {
    
    /** Auto-generated primary key for this invitation record. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_id")
    private int taskInvitationId;

    /** The task this invitation was issued for. */
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskInvoice taskId;

    /** The helper who was invited to the task. */
    @ManyToOne
    @JoinColumn(name = "helper_id")
    private Helper helperId;  

    /**
     * Current status of the invitation.
     * Expected values: "Invited", "Accepted", "Declined", "Rejected".
     */

    @Column(name = "status")
    private String status;

    /** Timestamp indicating when the invitation was sent. */
    @Column(name = "invited_at")
    private Date invitedAt;

    /**
     * Default no-args constructor required by JPA.
     */
    public TaskInvitation() {
    }


    /**
     * Constructs a fully-populated {@code TaskInvitation}.
     *
     * @param taskInvitationId the primary key of the invitation
     * @param taskId           the task the invitation relates to
     * @param helperId         the helper being invited
     * @param status           the current invitation status
     * @param invitedAt        the timestamp the invitation was sent
     */
    public TaskInvitation(int taskInvitationId, TaskInvoice taskId, Helper helperId, String status, Date invitedAt) {
        this.taskInvitationId = taskInvitationId;
        this.taskId = taskId;
        this.helperId = helperId;
        this.status = status;
        this.invitedAt = invitedAt;
    }
}
