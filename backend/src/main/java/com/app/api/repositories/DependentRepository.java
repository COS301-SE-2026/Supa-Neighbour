package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Dependent;
@Repository
public interface DependentRepository extends JpaRepository<Dependent, Integer> {
    
}
