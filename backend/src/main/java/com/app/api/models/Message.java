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
 * Represents a single message within a chat thread.
 */
@Entity
@Table(name = "message_table")
public class Message {

    /** The message ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private int messageId;

    /** The chat this message belongs to. */
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    /** The user who sent this message. */
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    /** The message content — text or image URL. */
    @Column(name = "content")
    private String content;

    /** The message type — 'text' or 'image'. */
    @Column(name = "message_type")
    private String messageType;

    /** Whether this message has been read by the recipient. */
    @Column(name = "is_read")
    private boolean isRead;

    /** When the message was sent. */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /**
     * Default constructor required by JPA.
     */
    public Message() {
    }

    /**
     * Gets the message ID.
     * @return the message ID
     */
    public int getMessageId() {
        return messageId;
    }

    /**
     * Sets the message ID.
     * @param messageId the message ID
     */
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the chat.
     * @return the chat
     */
    public Chat getChat() {
        return chat;
    }

    /**
     * Sets the chat.
     * @param chat the chat
     */
    public void setChat(Chat chat) {
        this.chat = chat;
    }

    /**
     * Gets the sender.
     * @return the sender
     */
    public User getSender() {
        return sender;
    }

    /**
     * Sets the sender.
     * @param sender the sender
     */
    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Gets the message content.
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the message content.
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the message type.
     * @return the message type
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * Sets the message type.
     * @param messageType the message type
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    /**
     * Returns whether the message has been read.
     * @return true if read
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets whether the message has been read.
     * @param isRead true if read
     */
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    /**
     * Gets the sent timestamp.
     * @return the sent timestamp
     */
    public LocalDateTime getSentAt() {
        return sentAt;
    }

    /**
     * Sets the sent timestamp.
     * @param sentAt the sent timestamp
     */
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
