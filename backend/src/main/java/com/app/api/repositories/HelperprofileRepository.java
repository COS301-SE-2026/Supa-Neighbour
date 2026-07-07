package com.app.api.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public class HelperprofileRepository {
    @PersistenceContext
    private EntityManager em;

    /**
     * Returns core helper info:
     * [helper_id, display_name, trust_score, neighbourhood_id]
     * Returns null if helper does not exist.
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
     * Returns the helper's rank within their neighbourhood (by average_rating).
     * Used to derive the podium level (Gold/Silver/Bronze).
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

    public int CompletedTasks(int helperId){
        String sql = """
                SELECT COUNT(*) FROM task_invoice_table
                WHERE helper_id = :helperId
                  AND status    = 'completed'
                """;

        return ((Number)  em.createNativeQuery(sql).setParameter("helperId", helperId).getSingleResult()).intValue();
    }

    public int countNeighboursHelped(int helperId){
        String sql = """
                SELECT COUNT(DISTINCT dependent_id)
                FROM task_invoice_table
                WHERE helper_id = :helperId
                  AND status    = 'completed'
                """;

        return ((Number) em.createNativeQuery(sql).setParameter("helperId", helperId).getSingleResult()).intValue();
    }

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
