package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import com.app.api.models.User;
import com.app.api.models.Badges;
import java.util.Date;
/**
 * Represents a user achievement within the application.
 * <p>
 * This entity maps to the {@code user_achievement_table} database table and
 * stores references to a user, achievement, and associated metadata.
 * </p>
 */
@Data
@Builder
@Entity
@Table(name = "task_invitation_table")
public class TaskInvitation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_invitation_id")
    private int taskInvitationId;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskInvoice taskId;

    @ManyToOne
    @JoinColumn(name = "helper_id")
    private User helperId;  

    @Column(name = "status")
    private String status;

    @Column(name = "invited_at")
    private Date invitedAt;

    public TaskInvitation() {
    }

    public TaskInvitation(int taskInvitationId, TaskInvoice taskId, User helperId, String status, Date invitedAt) {
        this.taskInvitationId = taskInvitationId;
        this.taskId = taskId;
        this.helperId = helperId;
        this.status = status;
        this.invitedAt = invitedAt;
    }
}
