package com.app.api.repositories;

import com.app.api.models.Admin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for Admin entities.
 */
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    /**
     * Returns the admin with the fewest reports currently in {@code assigned} status.
     *
     * @return the admin with the least assigned workload, or empty if no admins exist
     */
    @Query(value = """
            SELECT a.*
            FROM admin_table a
            LEFT JOIN report_table r
                ON r.admin_id = a.user_id AND r.status = 'assigned'
            GROUP BY a.admin_id
            ORDER BY COUNT(r.report_id) ASC, a.admin_id ASC
            LIMIT 1
            """, nativeQuery = true)
    Optional<Admin> findAdminWithLeastAssignedReports();
}

