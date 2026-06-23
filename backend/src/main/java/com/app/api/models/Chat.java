package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Represents a chat thread between a dependent and a helper for a specific task.
 */
@Entity
@Table(name = "chat_table")
public class Chat {

    /** The chat ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private int chatId;

    /** The task this chat is linked to. */
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskInvoice task;

    /** The dependent user in the chat. */
    @ManyToOne
    @JoinColumn(name = "dependent_user_id")
    private User dependentUser;

    /** The helper user in the chat. */
    @ManyToOne
    @JoinColumn(name = "helper_user_id")
    private User helperUser;

    /** When the chat was created. */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Default constructor required by JPA.
     */
    public Chat() {
    }

    /**
     * Gets the chat ID.
     * @return the chat ID
     */
    public int getChatId() {
        return chatId;
    }

    /**
     * Sets the chat ID.
     * @param chatId the chat ID
     */
    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    /**
     * Gets the task.
     * @return the task
     */
    public TaskInvoice getTask() {
        return task;
    }

    /**
     * Sets the task.
     * @param task the task
     */
    public void setTask(TaskInvoice task) {
        this.task = task;
    }

    /**
     * Gets the dependent user.
     * @return the dependent user
     */
    public User getDependentUser() {
        return dependentUser;
    }

    /**
     * Sets the dependent user.
     * @param dependentUser the dependent user
     */
    public void setDependentUser(User dependentUser) {
        this.dependentUser = dependentUser;
    }

    /**
     * Gets the helper user.
     * @return the helper user
     */
    public User getHelperUser() {
        return helperUser;
    }

    /**
     * Sets the helper user.
     * @param helperUser the helper user
     */
    public void setHelperUser(User helperUser) {
        this.helperUser = helperUser;
    }

    /**
     * Gets the creation timestamp.
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
