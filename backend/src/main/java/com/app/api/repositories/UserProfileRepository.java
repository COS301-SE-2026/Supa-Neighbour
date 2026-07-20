package com.app.api.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;


/**
 * Repository responsible for performing custom database operations
 * related to user profiles, helper information, achievements,
 * and recent task history.
 */
@Repository
public class UserProfileRepository {
    @PersistenceContext
    private EntityManager em;

     /**
     * Retrieves the core profile information for a user.
     *
     * @param userId the identifier of the user
     * @return an array containing the user ID, display name,
     *         neighbourhood name, and neighbourhood ID, or
     *         {@code null} if the user does not exist
     */
    public Object[] findUserCore(int userId){
        String sql = """
                SELECT
                    u.user_id,
                    u.user_name || ' ' || LEFT(u.user_surname, 1) || '.' AS display_name,
                    l.neighbourhood_name,
                    l.location_id                                          AS neighbourhood_id
                FROM user_table     u
                JOIN address_table  a ON a.address_id  = u.user_address_id
                JOIN location_table l ON l.location_id = a.neighbourhood_id
                WHERE u.user_id = :userId
                """;

        try{
            return (Object[]) em.createNativeQuery(sql).setParameter("userId", userId).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

     /**
     * Retrieves helper-specific information for a user.
     *
     * @param userId the identifier of the user
     * @return an array containing the helper ID, experience points,
     *         and average rating, or {@code null} if the user is
     *         not registered as a helper
     */
    public Object[] findHelperData(int userId){
        String sql = """
                SELECT
                    h.helper_id,
                    h.helper_xp,
                    COALESCE(ha.average_rating, 0.0) AS average_rating
                FROM helper_table             h
                LEFT JOIN helper_analytics_table ha ON ha.user_id = h.user_id
                WHERE h.user_id = :userId
                """;

        try{
            return (Object[]) em.createNativeQuery(sql).setParameter("userId", userId).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }


    /**
     * Retrieves the helper's ranking within a neighbourhood.
     *
     * @param helperId the identifier of the helper
     * @param neighbourhoodId the identifier of the neighbourhood
     * @return the helper's ranking, or {@code 0} if no ranking exists
     */
    public int findHelperRank(int helperId, int neighbourhoodId){
        String sql = """
                SELECT ranked.rank
                FROM (
                    SELECT
                        h.helper_id,
                        RANK() OVER (ORDER BY COALESCE(ha.average_rating, 0.0) DESC) AS rank
                    FROM helper_table             h
                    JOIN user_table               u  ON u.user_id    = h.user_id
                    JOIN address_table            a  ON a.address_id = u.user_address_id
                    LEFT JOIN helper_analytics_table ha ON ha.user_id = h.user_id
                    WHERE a.neighbourhood_id = :neighbourhoodId
                ) ranked
                WHERE ranked.helper_id = :helperId
                """;

        try{
            return ((Number) em.createNativeQuery(sql)
                    .setParameter("neighbourhoodId", neighbourhoodId)
                    .setParameter("helperId", helperId)
                    .getSingleResult()).intValue();
        }catch(NoResultException e){
            return 0;
        }
    }


    /**
     * Retrieves the skills associated with a helper.
     *
     * @param helperId the identifier of the helper
     * @return a list of skill descriptions assigned to the helper
     */
    @SuppressWarnings("unchecked")
    public List<String> findSkills(int helperId){
        String sql = """
                SELECT tt.type_description
                FROM helper_skill_table hs
                JOIN task_type_table    tt ON tt.task_type_id = hs.task_type_id
                WHERE hs.helper_id = :helperId
                ORDER BY tt.type_description ASC
                """;

        return em.createNativeQuery(sql).setParameter("helperId", helperId).getResultList();
    }

    /**
     * Retrieves the achievements earned by a user.
     *
     * @param userId the identifier of the user
     * @return a list of earned achievement records
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findEarnedAchievements(int userId){
        String sql = """
                SELECT
                    b.badge_id,
                    b.badge_name,
                    b.badge_description,
                    ua.awarded_on
                FROM user_achievement_table ua
                JOIN badge_table            b  ON b.badge_id = ua.badge_id
                JOIN user_table             u  ON u.user_id  = ua.user_id
                WHERE ua.user_id    = :userId
                  AND ua.awarded_on IS NOT NULL
                  AND u.user_type  != 'Admin'
                ORDER BY ua.awarded_on DESC
                """;

        return em.createNativeQuery(sql).setParameter("userId", userId).getResultList();
    }

     /**
     * Counts the number of completed tasks performed by a helper.
     *
     * @param helperId the identifier of the helper
     * @return the number of completed tasks
     */
    public int countCompletedTasks(int helperId){
        String sql = """
                SELECT COUNT(*) FROM task_invoice_table
                WHERE helper_id = :helperId
                  AND status    = 'completed'
                """;

        return ((Number)em.createNativeQuery(sql).setParameter("helperId", helperId).getSingleResult()).intValue();
    }

    /**
     * Retrieves the helper's most recently completed tasks.
     *
     * @param helperId the identifier of the helper
     * @return a list containing up to five recently completed task records
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findRecentTasks(int helperId){
        String sql = """
                SELECT
                    ti.task_id,
                    tt.type_description,
                    ti.end_date
                FROM task_invoice_table  ti
                JOIN task_type_table     tt ON tt.task_type_id = ti.task_type_id
                WHERE ti.helper_id = :helperId
                  AND ti.status    = 'completed'
                ORDER BY ti.end_date DESC
                LIMIT 5
                """;

        return em.createNativeQuery(sql).setParameter("helperId", helperId).getResultList();
    }

    
}
