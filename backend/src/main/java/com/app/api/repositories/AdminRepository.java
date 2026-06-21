package com.app.api.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Admin;

/**
 * Repository for Admin entities.
 */
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    
} 
