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
     * Determines whether the supplied star rating is within the valid range.
     *
     * @param rating the rating value to validate
     * @return {@code true} if 1 <= rating <= 5; otherwise {@code false}
     */
    public boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }

    /**
     * Recalculates the average rating for the specified helper.
     *
     * <p>
     * The average is computed directly from the numeric
     * {@code dependent_rating_review} values on the helper's tasks and
     * is stored in the helper analytics table.
     * </p>
     *
     * @param helperId the identifier of the helper whose average rating
     *                 is to be recalculated
     */
    public void recalculateAverageRating(int helperId, int rating) {
        String getHelperUserIdSql = """
                SELECT user_id FROM helper_table WHERE helper_id = :helperId
                """;

        int helperUserId = ((Number) em.createNativeQuery(getHelperUserIdSql).setParameter("helperId", helperId)
                .getSingleResult()).intValue();

        double currentAverage = findAverageRating(helperId);
        int ratingCount = countCompletedRatingsForHelper(helperId);

        double newAverage;
        if (currentAverage <= 0 || ratingCount == 0) {
            newAverage = rating;
        } else {
            newAverage = ((currentAverage * ratingCount) + rating) / (ratingCount + 1.0);
        }

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

    /**
     * Counts how many completed tasks for the specified helper have a
     * dependent rating recorded.
     *
     * @param helperId the identifier of the helper whose completed ratings
     *                 are being counted
     * @return the number of completed task invoices for the helper that have a
     *         non-null dependent rating review
     */
    public int countCompletedRatingsForHelper(int helperId) {
        String sql = """
            SELECT COUNT(*)
            FROM task_invoice_table ti
            WHERE ti.helper_id = :helperId
            AND ti.status = 'completed'
            AND ti.dependent_rating_review IS NOT NULL
            """;

        Number count = (Number) em.createNativeQuery(sql)
            .setParameter("helperId", helperId)
            .getSingleResult();

        return count == null ? 0 : count.intValue();
    }
}
