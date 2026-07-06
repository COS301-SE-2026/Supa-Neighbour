package com.app.api.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
 
import java.util.List;

@Repository
public class RatingRepository {
    @PersistenceContext
    private EntityManager em;

     /**
     * Returns [task_id, helper_id, dependent_rating_review, end_date] for a task.
     * Returns null if the task does not exist.
     */

     public Object[] findTaskById(int taskId){
        String sql = """
                SELECT
                ti.task_id,
                ti.helper_id,
                ti.dependent_rating_review,
                ti.status                     
                FROM task_invoice_table ti
                WHERE ti.task_id = :taskId
                """;

        try{
            return (Object[]) em.createNativeQuery(sql).setParameter("taskId", taskId).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    /**
     * Returns the user_type of the authenticated user.
     * Used to block admins from submitting ratings.
     */
    public String findUserType(int userId){
        String sql = "SELECT user_type FROM user_table WHERE user_id = :userId";

        try{
            return (String) em.createNativeQuery(sql).setParameter("userId", userId).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    /**
     * Returns the dependent's user_id for a given task.
     * Used to verify the caller is the requester for this task.
     */

    public Integer findDependentUserId(int taskId){
        String sql = """
                    SELECT u.user_id
                        FROM task_invoice_table ti
                        JOIN dependent_table    d ON d.dependent_id = ti.dependent_id
                        JOIN user_table         u ON u.user_id      = d.user_id
                        WHERE ti.task_id = :taskId
                """;

        try{
            return ((Number) em.createNativeQuery(sql).setParameter("taskId", taskId).getSingleResult()).intValue();
        }catch(NoResultException e){
            return null;
        }
        
    }

    /**
     * Checks that the rating value exists in rating_table.
     */
    public boolean isValidRating(String rating){
        String sql = "SELECT COUNT(*) FROM rating_table WHERE rating_review = :rating";
        
        long count = ((Number) em.createNativeQuery(sql).setParameter("rating", rating).getSingleResult()).longValue();
        return count > 0;
    }

    /**
     * Writes the rating and review snippet to task_invoice_table.
     */
    public void submitRating(int taskId, String rating, String reviewSnippet){
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
     * Recalculates helper_analytics_table.average_rating for the given helper
     * based on all non-null dependent_rating_review values in task_invoice_table.
     *
     * Rating categories are converted to numeric scores:
     *   Outstanding = 5.0, Excellent = 4.0, Very Good = 3.0, Good = 2.0, Average = 1.0
     */

    public void recalculateAverageRating(int helperId){
        String getHelperUserIdSql ="""
                SELECT user_id FROM helper_table WHERE helper_id = :helperId
                """;

        int helperUserId = ((Number) em.createNativeQuery(getHelperUserIdSql).setParameter("helperId", helperId).getSingleResult()).intValue();

        String avgSql ="""
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

        if(result == null){
            return;
        }

        double newAverage = ((Number) result).doubleValue();

        String updateSql = """
                UPDATE helper_analytics_table
                SET average_rating = :newAverage
                WHERE user_id = :helperUserId
                """;

        em.createNativeQuery(updateSql).setParameter("newAverage", newAverage).setParameter("helperUserId", helperUserId).executeUpdate();
   }
}
