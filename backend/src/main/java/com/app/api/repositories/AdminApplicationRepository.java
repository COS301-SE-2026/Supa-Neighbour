package com.app.api.repositories;

import com.app.api.models.AdminApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link AdminApplication} persistence operations.
 */
@Repository
public interface AdminApplicationRepository extends JpaRepository<AdminApplication, Integer> {

    /**
     * Finds all applications submitted by a given user.
     *
     * @param userId the ID of the applying user
     * @return the list of applications for that user
     */
    List<AdminApplication> findByUser_UserId(Integer userId);

    /**
     * Finds all applications with the given status (e.g. "Pending", "Approved", "Rejected").
     *
     * @param applicationStatus the status to filter by
     * @return the list of matching applications
     */
    List<AdminApplication> findByApplicationStatus(String applicationStatus);
}
