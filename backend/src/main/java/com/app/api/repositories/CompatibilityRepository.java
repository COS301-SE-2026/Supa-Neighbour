package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Compatibility;
@Repository
public interface CompatibilityRepository extends JpaRepository<Compatibility, Integer> {
    
}
