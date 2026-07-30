package com.app.api.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
 
import java.util.List;

@Repository
public class AchievementRepository {
    @PersistenceContext
    private EntityManager em;

    /**
     * Returns all earned achievements for a user (awarded_on IS NOT NULL).
     * Joins user_achievement_table → badge_table for name and description.
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findEarned(int userId){
        String sql = """
                SELECT
                    b.badge_id,
                    b.badge_name,
                    b.badge_description,
                    ua.awarded_on
                FROM user_achievement_table ua
                JOIN badge_table b ON b.badge_id = ua.badge_id
                WHERE ua.user_id    = :userId
                  AND ua.awarded_on IS NOT NULL
                ORDER BY ua.awarded_on DESC
                """;

        return em.createNativeQuery(sql).setParameter("userId", userId).getResultList();
    }


    /**
     * Returns all unearned achievements for a user (awarded_on IS NULL).
     * Joins user_achievement_table → badge_table for name and description.
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findUnearned(int userId){
        String sql = """
                SELECT
                    b.badge_id,
                    b.badge_name,
                    b.badge_description,
                    ua.progress_current,
                    ua.progress_target
                FROM user_achievement_table ua
                JOIN badge_table b ON b.badge_id = ua.badge_id
                WHERE ua.user_id    = :userId
                  AND ua.awarded_on IS NULL
                ORDER BY b.badge_id ASC
                """;

        return em.createNativeQuery(sql).setParameter("userId", userId).getResultList();
    }
}
