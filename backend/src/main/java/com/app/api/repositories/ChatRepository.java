package com.app.api.repositories;

import com.app.api.models.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for chat thread data access.
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    /**
     * Finds all chats where the user is either the dependent or the helper.
     * @param dependentUserId the user ID to check as dependent
     * @param helperUserId the user ID to check as helper
     * @return list of chats involving the user
     */
    List<Chat> findByDependentUser_UseridOrHelperUser_Userid(int dependentUserId, int helperUserId);
}
