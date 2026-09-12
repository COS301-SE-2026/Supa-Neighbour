package com.app.api.repositories;

import com.app.api.models.AdminApplication;
import com.app.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminApplicationRepository extends JpaRepository<AdminApplication, Integer> {

    /**
     * Find all applications submitted by a specific user, most recent first.
     *
     * @param user the user who submitted the applications
     * @return list of the user's applications
     */
    List<AdminApplication> findByUserOrderByApplicationDateDesc(User user);
}
