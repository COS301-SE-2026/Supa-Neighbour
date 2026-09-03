package com.app.api.repositories;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class UserDeviceRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Registers or refreshes an FCM token for a user.
     * If the token already exists (same device re-registering), updates the
     * owning user and timestamp rather than inserting a duplicate row.
     *
     * @param userId the user registering the device
     * @param fcmToken the FCM token to upsert
     */
    @Transactional
    public void upsertToken(int userId, String fcmToken){
        Query query = entityManager.createNativeQuery(
            "INSERT INTO user_device_table (user_id, fcm_token, updated_at) " +
            "VALUES (:userId, :fcmToken, now()) " +
            "ON CONFLICT (fcm_token) DO UPDATE SET user_id = :userId, updated_at = now()"
        );

        query.setParameter("userId", userId);
        query.setParameter("fcmToken", fcmToken);
        query.executeUpdate();
    }

    /**
     * Fetches all FCM tokens currently registered for a user, across all their devices.
     *
     * @param userId the user whose tokens to fetch
     * @return list of FCM token strings
     */
    @SuppressWarnings("unchecked")
    public List<String> findTokensByUserId(int userId){
        Query query = entityManager.createNativeQuery(
            "SELECT fcm_token FROM user_device_table WHERE user_id = :userId"
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    /**
     * Deletes a dead/unregistered token, e.g. after FCM reports UNREGISTERED.
     *
     * @param fcmToken the token to remove
     */
    @Transactional
    public void deleteToken(String fcmToken){
        Query query = entityManager.createNativeQuery(
            "DELETE FROM user_device_table WHERE fcm_token = :fcmToken"
        );
        query.setParameter("fcmToken", fcmToken);
        query.executeUpdate();
    }
}
