package com.app.api.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
 
/**
 * Repository responsible for performing custom database operations
 * related to public helper profiles, rankings, skills, and reviews.
 */
@Repository
public class HelperprofileRepository {

    
    @PersistenceContext
    private EntityManager em;

     /**
     * Retrieves the core profile information for a helper.
     *
     * @param helperId the identifier of the helper
     * @return an array containing the helper ID, display name,
     *         trust score, and neighbourhood ID, or {@code null}
     *         if the helper does not exist
     */
    public Object[] findHelperCore(int helperId){
        String sql = """
                SELECT
                    h.helper_id,
                    u.user_name || ' ' || LEFT(u.user_surname, 1) || '.' AS display_name,
                    COALESCE(ha.average_rating, 0.0)                     AS trust_score,
                    a.neighbourhood_id
                FROM helper_table            h
                JOIN user_table              u  ON u.user_id    = h.user_id
                JOIN address_table           a  ON a.address_id = u.user_address_id
                LEFT JOIN helper_analytics_table ha ON ha.user_id = h.user_id
                WHERE h.helper_id = :helperId
                """;

        try{
            return (Object[]) em.createNativeQuery(sql).setParameter("helperId", helperId).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

   /**
     * Retrieves the helper's ranking within a neighbourhood.
     *
     * <p>The ranking is determined by the helper's average rating
     * relative to other helpers in the same neighbourhood.</p>
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
            return ((Number) em.createNativeQuery(sql).setParameter("neighbourhoodId", neighbourhoodId).setParameter("helperId", helperId).getSingleResult()).intValue();
        }catch(NoResultException e){
            return 0;
        }
    }

    /**
     * Counts the number of completed tasks performed by a helper.
     *
     * @param helperId the identifier of the helper
     * @return the number of completed tasks
     */
    public int CompletedTasks(int helperId){
        String sql = """
                SELECT COUNT(*) FROM task_invoice_table
                WHERE helper_id = :helperId
                  AND status    = 'completed'
                """;

        return ((Number)  em.createNativeQuery(sql).setParameter("helperId", helperId).getSingleResult()).intValue();
    }

    /**
     * Counts the number of unique neighbours assisted by a helper.
     *
     * @param helperId the identifier of the helper
     * @return the number of distinct neighbours helped
     */
    public int countNeighboursHelped(int helperId){
        String sql = """
                SELECT COUNT(DISTINCT dependent_id)
                FROM task_invoice_table
                WHERE helper_id = :helperId
                  AND status    = 'completed'
                """;

        return ((Number) em.createNativeQuery(sql).setParameter("helperId", helperId).getSingleResult()).intValue();
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
     * Retrieves the reviews received by a helper.
     *
     * @param helperId the identifier of the helper
     * @return a list of review records ordered by completion date,
     *         with the most recent reviews first
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findReviews(int helperId){
        String sql = """
                SELECT
                    ti.dependent_rating_review,
                    ti.review_snippet,
                    ti.end_date
                FROM task_invoice_table ti
                WHERE ti.helper_id     = :helperId
                  AND ti.review_snippet IS NOT NULL
                ORDER BY ti.end_date DESC
                """;

        return em.createNativeQuery(sql).setParameter("helperId", helperId).getResultList();
    }

    
}
