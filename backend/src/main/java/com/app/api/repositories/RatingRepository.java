package com.app.api.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for performing custom database operations
 * related to task ratings and helper rating analytics.
 */
@Repository
public class RatingRepository {
    @PersistenceContext
    private EntityManager em;

    /**
     * Retrieves information about the specified task.
     *
     * @param taskId the identifier of the task
     * @return an array containing the task ID, helper ID, dependent rating,
     *         and task status, or {@code null} if the task does not exist
     */
    public Object[] findTaskById(int taskId) {
        String sql = """
                SELECT
                ti.task_id,
                ti.helper_id,
                ti.dependent_rating_review,
                ti.status
                FROM task_invoice_table ti
                WHERE ti.task_id = :taskId
                """;

        try {
            return (Object[]) em.createNativeQuery(sql).setParameter("taskId", taskId).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Retrieves the type of the specified user.
     *
     * @param userId the identifier of the user
     * @return the user's type, or {@code null} if the user does not exist
     */
    public String findUserType(int userId) {
        String sql = "SELECT user_type FROM user_table WHERE user_id = :userId";

        try {
            return (String) em.createNativeQuery(sql).setParameter("userId", userId).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Retrieves the dependent user's identifier for the specified task.
     *
     * <p>
     * This is used to verify that the authenticated user is the
     * requester associated with the task.
     * </p>
     *
     * @param taskId the identifier of the task
     * @return the dependent user's identifier, or {@code null} if no
     *         dependent is associated with the task
     */
    public Integer findDependentUserId(int taskId) {
        String sql = """
                    SELECT u.user_id
                        FROM task_invoice_table ti
                        JOIN dependent_table    d ON d.dependent_id = ti.dependent_id
                        JOIN user_table         u ON u.user_id      = d.user_id
                        WHERE ti.task_id = :taskId
                """;

        try {
            return ((Number) em.createNativeQuery(sql).setParameter("taskId", taskId).getSingleResult()).intValue();
        } catch (NoResultException e) {
            return null;
        }

    }

    /**
     * Determines whether the supplied rating is valid.
     *
     * @param rating the rating value to validate
     * @return {@code true} if the rating exists in the rating table;
     *         otherwise {@code false}
     */
    public boolean isValidRating(String rating) {
        String sql = "SELECT COUNT(*) FROM rating_table WHERE rating_review = :rating";

        long count = ((Number) em.createNativeQuery(sql).setParameter("rating", rating).getSingleResult()).longValue();
        return count > 0;
    }

    /**
     * Stores a rating and optional review snippet for the specified task.
     *
     * @param taskId        the identifier of the task
     * @param rating        the rating submitted by the dependent
     * @param reviewSnippet the accompanying review snippet, if provided
     */
    public void submitRating(int taskId, String rating, String reviewSnippet) {
        String sql = """
                UPDATE task_invoice_table
                SET dependent_rating_review = :rating,
                    review_snippet          = :reviewSnippet
                WHERE task_id = :taskId
                """;
        em.createNativeQuery(sql).setParameter("rating", rating).setParameter("reviewSnippet", reviewSnippet)
                .setParameter("taskId", taskId).executeUpdate();
    }

    /**
     * Recalculates the average rating for the specified helper.
     *
     * <p>
     * The average is computed from all submitted dependent ratings
     * associated with the helper's completed tasks and is stored in
     * the helper analytics table.
     * </p>
     *
     * @param helperId the identifier of the helper whose average rating
     *                 is to be recalculated
     */
    public void recalculateAverageRating(int helperId) {
        String getHelperUserIdSql = """
                SELECT user_id FROM helper_table WHERE helper_id = :helperId
                """;

        int helperUserId = ((Number) em.createNativeQuery(getHelperUserIdSql).setParameter("helperId", helperId)
                .getSingleResult()).intValue();

        String avgSql = """
                      SELECT AVG(
                    CASE r.rating_review
                        WHEN 'Outstanding' THEN 5.0
                        WHEN 'Excellent'   THEN 4.0
                        WHEN 'Very Good'   THEN 3.0
                        WHEN 'Good'        THEN 2.0
                        WHEN 'Average'     THEN 1.0
                    END
                )
                FROM task_invoice_table ti
                JOIN rating_table       r ON r.rating_review = ti.dependent_rating_review
                WHERE ti.helper_id                 = :helperId
                  AND ti.dependent_rating_review   IS NOT NULL
                """;

        Object result = em.createNativeQuery(avgSql).setParameter("helperId", helperId).getSingleResult();

        if (result == null) {
            return;
        }

        double newAverage = ((Number) result).doubleValue();

        String updateSql = """
                UPDATE helper_analytics_table
                SET average_rating = :newAverage
                WHERE user_id = :helperUserId
                """;

        em.createNativeQuery(updateSql).setParameter("newAverage", newAverage)
                .setParameter("helperUserId", helperUserId).executeUpdate();
    }

    /**
     * Retrieves the average rating for the specified helper.
     *
     * @param helperId the identifier of the helper
     * @return the helper's average rating, or {@code null} if none exists
     */
    public Double findAverageRating(int helperId) {
        String sql = """
        SELECT  ha.average_rating
        FROM helper_table h
        JOIN helper_analytics_table ha ON ha.user_id = h.user_id
        WHERE h.helper_id = :helperId
        """;
        try{
            Object result = em.createNativeQuery(sql).setParameter("helperId", helperId).getSingleResult();
            return result != null? ((Number) result).doubleValue():null;
        } catch(NoResultException e){
            return null;
        }
    }
}
