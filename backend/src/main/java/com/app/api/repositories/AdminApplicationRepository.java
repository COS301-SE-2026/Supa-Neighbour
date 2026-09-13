package com.app.api.repositories;

import com.app.api.models.AdminApplication;
import com.app.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for AdminApplication entities.
 */
@Repository
public interface AdminApplicationRepository extends JpaRepository<AdminApplication, Integer> {

    /**
     * Find all applications submitted by a specific user, most recent first.
     *
     * @param user the user who submitted the applications
     * @return list of the user's applications
     */
    List<AdminApplication> findByUserOrderByApplicationDateDesc(User user);

    /**
     * Find all applications with their applicant eagerly loaded, most recent first.
     *
     * @return list of all applications
     */
    @Query("SELECT a FROM AdminApplication a JOIN FETCH a.user ORDER BY a.applicationDate DESC")
    List<AdminApplication> findAllByOrderByApplicationDateDesc();

    /**
     * Find all applications with a specific status, with their applicant eagerly loaded,
     * most recent first.
     *
     * @param status the status to filter by
     * @return list of matching applications
     */
    @Query("SELECT a FROM AdminApplication a JOIN FETCH a.user "
            + "WHERE a.applicationStatus = :status ORDER BY a.applicationDate DESC")
    List<AdminApplication> findByApplicationStatusOrderByApplicationDateDesc(
            @Param("status") String status);


    /**
     * Check if a user has an existing application with a given status.
     *
     * @param user   the user to check
     * @param status the status to check for
     * @return true if a matching application exists
     */
     boolean existsByUserAndApplicationStatus(User user, String status);

}
