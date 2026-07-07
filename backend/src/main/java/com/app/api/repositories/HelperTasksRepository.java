package com.app.api.repositories;
 
import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
 
@Repository
public class HelperTasksRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * Looks up the helper_id for the authenticated user.
     * Returns null if the user is not registered as a helper.
     */
    public Integer findHelperByUserId(int userId){
        String sql = """
                SELECT helper_id FROM helper_table WHERE user_id = :userId
                """;
        try{
            return ((Number) em.createNativeQuery(sql).setParameter("userId", userId).getSingleResult()).intValue();
        } catch(NoResultException e){
            return null;
        }
    }

    /**
     * Returns tasks from task_invitation_table only.
     * These are tasks the helper has expressed interest in but have NOT yet
     * moved to task_invoice_table (i.e. still Accepted, Rejected, or Declined).
     * Only shown when the task is still 'open' to avoid duplicates with
     * findAssignedTasks.
     *
     * Columns: task_id, task_type, status, start_date, end_date, neighbourhood_name, xp_worth
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findInvitedTasks(int helperId, String statusFilter, int limit, int offset){
        String statusClause = statusFilter != null ? "AND ti.status = :status" : "AND ti.status IN ('Invited', 'Declined', 'Accepted', 'Rejected')";
        String sql  = """
                SELECT
                    tit.task_id,
                    tt.type_description   AS task_type,
                    ti.status,
                    tit.start_date,
                    tit.end_date,
                    l.neighbourhood_name,
                    tt.xp_worth
                FROM task_invitation_table  ti
                JOIN task_invoice_table     tit ON tit.task_id     = ti.task_id
                JOIN task_type_table        tt  ON tt.task_type_id = tit.task_type_id
                JOIN location_table         l   ON l.location_id   = tit.location_id
                WHERE ti.helper_id  = :helperId
                  AND tit.status    = 'open'
                """ + statusClause + """
                ORDER BY tit.start_date DESC
                LIMIT :limit OFFSET :offset
                """;

        var query = em.createNativeQuery(sql).setParameter("helperId", helperId).setParameter("limit", limit).setParameter("offset", offset);
        if(statusFilter != null){
            query.setParameter("status", statusFilter);
        }
        return query.getResultList();
    }


    /**
     * Returns tasks from task_invoice_table only.
     * These are tasks past 'open' — helper has been assigned and work
     * is in progress or done. Never overlaps with findInvitationTasks.
     *
     * Columns: task_id, task_type, status, start_date, end_date, neighbourhood_name, xp_worth
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findAssignedTasks(int helperId, String statusFilter,
                                            int limit, int offset) {
        String statusClause = statusFilter != null
                ? "AND ti.status = :status"
                : "AND ti.status IN ('assigned', 'in_progress', 'pending_approval', 'completed', 'cancelled')";
 
        String sql = """
                SELECT
                    ti.task_id,
                    tt.type_description   AS task_type,
                    ti.status,
                    ti.start_date,
                    ti.end_date,
                    l.neighbourhood_name,
                    tt.xp_worth
                FROM task_invoice_table  ti
                JOIN task_type_table     tt ON tt.task_type_id = ti.task_type_id
                JOIN location_table      l  ON l.location_id   = ti.location_id
                WHERE ti.helper_id = :helperId
                """ + statusClause + """
                ORDER BY ti.start_date DESC
                LIMIT :limit OFFSET :offset
                """;
 
        var query = em.createNativeQuery(sql)
                .setParameter("helperId", helperId)
                .setParameter("limit",    limit)
                .setParameter("offset",   offset);
 
        if (statusFilter != null) {
            query.setParameter("status", statusFilter);
        }
 
        return query.getResultList();
    }


     /**
     * Total count across both tables for the given helper — used to populate
     * the "total" field in the response without loading all rows.
     */

     public int countAllTasks(int helperId){
        String sql = """
                SELECT COUNT(*) FROM (
                    SELECT ti.task_id
                    FROM task_invitation_table ti
                    JOIN task_invoice_table tit ON tit.task_id = ti.task_id
                    WHERE ti.helper_id = :helperId
                      AND tit.status   = 'open'
                    UNION ALL
                    SELECT task_id
                    FROM task_invoice_table
                    WHERE helper_id = :helperId
                      AND status   != 'open'
                ) combined
                """;

        return ((Number)  em.createNativeQuery(sql).setParameter("helperId", helperId).getSingleResult()).intValue();
     }

    


}