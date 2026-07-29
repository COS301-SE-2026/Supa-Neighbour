package com.app.api.repositories;

import com.app.api.models.Message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.Message;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for message data access.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    /**
     * Finds all messages for a chat ordered by sent time ascending, with pagination.
     * @param chatId the chat ID
     * @param pageable pagination parameters
     * @return page of messages
     */
    Page<Message> findByChat_ChatIdOrderBySentAtAsc(int chatId, Pageable pageable);

    /**
     * Counts unread messages in a chat that were not sent by the given user.
     * @param chatId the chat ID
     * @param senderId the user ID to exclude (don't count their own messages)
     * @return count of unread messages
     */
    long countByChat_ChatIdAndIsReadFalseAndSender_UseridNot(int chatId, int senderId);

    /**
     * Finds the last message in a chat.
     * @param chatId the chat ID
     * @param pageable use PageRequest.of(0, 1) to get just the last message
     * @return page containing the last message
     */
    Page<Message> findByChat_ChatIdOrderBySentAtDesc(int chatId, Pageable pageable);

    /**
     * Deletes all messages belonging to the given chat.
     * @param chatId the chat ID whose messages should be deleted
     */
    @Modifying
    @Transactional
    @Query("delete from Message m where m.chat.chatId = :chatId")
    void deleteByChatId(@Param("chatId") int chatId);

    /**
     * Marks all messages in a chat as read, excluding messages sent by the given user.
     * @param chatId the chat ID
     * @param userId the user ID whose messages should not be marked as read
     */
    @Modifying
    @Transactional
    @Query("update Message m set m.isRead = true where m.chat.chatId = :chatId and m.sender.userid != :userId")
    void markMessagesAsRead(@Param("chatId") int chatId, @Param("userId") int userId);

}
