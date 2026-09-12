package com.app.api.repositories;

import com.app.api.models.AdminApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminApplicationRepository extends JpaRepository<AdminApplication, Integer> {

}
