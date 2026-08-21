package com.app.api.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

/**
 * Repository responsible for performing custom database operations
 * related to helper task history and task statistics.
 */
@Repository
public class HelperTasksRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * Retrieves the helper identifier associated with the specified user.
     *
     * @param userId the identifier of the user
     * @return the helper identifier, or {@code null} if the user is not
     *         registered as a helper
     */
    public Integer findHelperByUserId(int userId) {
        String sql = """
                SELECT helper_id FROM helper_table WHERE user_id = :userId
                """;
        try {
            return ((Number) em.createNativeQuery(sql).setParameter("userId", userId).getSingleResult()).intValue();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Retrieves tasks from the invitation table for the specified helper.
     *
     * <p>
     * Only invitations for tasks that are still open are returned to
     * avoid duplication with assigned tasks. Results may optionally be
     * filtered by invitation status and paginated.
     * </p>
     *
     * @param helperId     the identifier of the helper
     * @param statusFilter an optional invitation status filter
     * @param limit        the maximum number of task records to return
     * @param offset       the number of task records to skip for pagination
     * @return a list of task records matching the supplied criteria
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findInvitedTasks(int helperId, String statusFilter, int limit, int offset) {
        String statusClause = statusFilter != null ? "AND ti.status = :status"
                : "AND ti.status IN ('Invited', 'Declined', 'Accepted', 'Rejected')";
        String sql = """
                SELECT
                    tit.task_id,
                    tt.type_description   AS task_type,
                    ti.status,
                    tit.start_date,
                    tit.end_date,
                    l.neighbourhood_name,
                    tt.xp_worth,
                    null AS admin_review,
                    u.user_name || ' ' || u.user_surname AS requester_name,
                    u.user_id AS requester_user_id
                FROM task_invitation_table  ti
                JOIN task_invoice_table     tit ON tit.task_id     = ti.task_id
                JOIN task_type_table        tt  ON tt.task_type_id = tit.task_type_id
                JOIN location_table         l   ON l.location_id   = tit.location_id
                LEFT JOIN dependent_table   d   ON d.dependent_id  = tit.dependent_id
                LEFT JOIN user_table        u   ON u.user_id        = d.user_id
                WHERE ti.helper_id  = :helperId
                AND tit.status    = 'open'
                """ + statusClause + """
                ORDER BY tit.start_date DESC
                LIMIT :limit OFFSET :offset
                """;

        var query = em.createNativeQuery(sql).setParameter("helperId", helperId).setParameter("limit", limit)
                .setParameter("offset", offset);
        if (statusFilter != null) {
            query.setParameter("status", statusFilter);
        }
        return query.getResultList();
    }

    /**
     * Retrieves tasks that a helper has accepted, covering all active and
     * historical invoice statuses.
     *
     * @param helperId the identifier of the helper
     * @param limit    the maximum number of task records to return
     * @param offset   the number of task records to skip for pagination
     * @return a list of task records for the helper
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findAcceptedTasks(int helperId, int limit, int offset) {
        String sql = """
                SELECT
                    ti.task_id,
                    tt.type_description AS task_type,
                    ti.status,
                    ti.start_date,
                    ti.end_date,
                    tt.xp_worth,
                    ti.helper_rating_review,
                    u.user_name || ' ' || u.user_surname AS requester_name,
                    u.user_id AS requester_user_id
                FROM task_invoice_table ti
                JOIN task_type_table tt ON tt.task_type_id = ti.task_type_id
                LEFT JOIN dependent_table d ON d.dependent_id = ti.dependent_id
                LEFT JOIN user_table u ON u.user_id = d.user_id
                WHERE ti.helper_id = :helperId
                AND ti.status IN ('assigned', 'in_progress', 'pending_approval', 'completed', 'cancelled')
                ORDER BY ti.start_date DESC
                LIMIT :limit OFFSET :offset
                """;
        return em.createNativeQuery(sql)
                .setParameter("helperId", helperId)
                .setParameter("limit", limit)
                .setParameter("offset", offset)
                .getResultList();
    }

    /**
     * Retrieves tasks that a helper has accepted and been assigned to.
     *
     * <p>
     * Returns tasks where the helper's invitation has been accepted and
     * the underlying task has moved to the {@code assigned} state.
     * </p>
     *
     * @param helperId the identifier of the helper
     * @param limit    the maximum number of task records to return
     * @param offset   the number of task records to skip for pagination
     * @return a list of accepted/assigned task records for the helper
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findAssignedTasks(int helperId, String statusFilter,
            int limit, int offset) {
            String statusClause = statusFilter != null
                    ? " AND ti.status = :status "
                    : " AND ti.status IN ('assigned', 'in_progress', 'pending_approval', 'completed', 'cancelled') ";
            String sql = """
                    SELECT
                        ti.task_id,
                        tt.type_description AS task_type,
                        ti.status,
                        ti.start_date,
                        ti.end_date,
                        tt.xp_worth,
                        ti.admin_review,
                        u.user_name || ' ' || u.user_surname AS requester_name,
                        u.user_id AS requester_user_id
                    FROM task_invoice_table ti
                    JOIN task_type_table tt ON tt.task_type_id = ti.task_type_id
                    LEFT JOIN dependent_table d ON d.dependent_id = ti.dependent_id
                    LEFT JOIN user_table u ON u.user_id = d.user_id
                    WHERE ti.helper_id = :helperId
                    """ + statusClause + """
                    ORDER BY ti.start_date DESC
                    LIMIT :limit OFFSET :offset
                    """;
        var query = em.createNativeQuery(sql)
                    .setParameter("helperId", helperId)
                    .setParameter("limit", limit)
                    .setParameter("offset", offset);

        if (statusFilter != null) {
            query.setParameter("status", statusFilter);
        }

        return query.getResultList();
    }

    /**
     * Counts tasks that a helper has accepted and been assigned to.
     *
     * @param helperId the identifier of the helper
     * @return the total number of accepted/assigned tasks for the helper
     */
    public int countAcceptedTasks(int helperId) {
        String sql = """
            SELECT COUNT(*)
            FROM task_invitation_table  ti
            JOIN task_invoice_table     tit ON tit.task_id = ti.task_id
            WHERE ti.helper_id  = :helperId
            AND ti.status     = 'Accepted'
            AND tit.status    = 'assigned'
            """;
        var query = em.createNativeQuery(sql).setParameter("helperId", helperId);
        return ((Number) query.getSingleResult()).intValue();
    }

    /**
     * Retrieves completed tasks for the specified helper.
     *
     * <p>
     * Only tasks where the helper's invitation was accepted and the
     * underlying task has reached the {@code completed} state are
     * returned. Results are paginated.
     * </p>
     *
     * @param helperId the identifier of the helper
     * @param limit    the maximum number of task records to return
     * @param offset   the number of task records to skip for pagination
     * @return a list of completed task records for the helper
     */
    public List<Object[]> findCompletedTasks(int helperId, int limit, int offset) {
        String sql = """
                SELECT
                    tit.task_id,
                    tt.type_description   AS task_type,
                    ti.status,
                    tit.start_date,
                    tit.end_date,
                    l.neighbourhood_name,
                    tt.xp_worth,
                    tit.admin_review,
                    u.user_name || ' ' || u.user_surname AS requester_name
                FROM task_invitation_table  ti
                JOIN task_invoice_table     tit ON tit.task_id     = ti.task_id
                JOIN task_type_table        tt  ON tt.task_type_id = tit.task_type_id
                JOIN location_table         l   ON l.location_id   = tit.location_id
                LEFT JOIN dependent_table   d   ON d.dependent_id  = tit.dependent_id
                LEFT JOIN user_table        u   ON u.user_id        = d.user_id
                WHERE ti.helper_id  = :helperId
                AND ti.status     = 'Accepted'
                AND tit.status    = 'completed'
                ORDER BY tit.start_date DESC
                LIMIT :limit OFFSET :offset
                """;

        var query = em.createNativeQuery(sql)
                .setParameter("helperId", helperId)
                .setParameter("limit", limit)
                .setParameter("offset", offset);

        @SuppressWarnings("unchecked")
        List<Object[]> result = (List<Object[]>) query.getResultList();
        return result;
    }



}
