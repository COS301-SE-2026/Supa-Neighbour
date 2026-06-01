package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Badges;
@Repository
public interface BadgesRepository extends JpaRepository<Badges, Integer> {
    
}
