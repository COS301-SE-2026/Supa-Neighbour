package com.app.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.Analytics;
import com.app.api.models.UserAchievement;

@Repository
public interface InvitRepository extends JpaRepository<Analytics, Integer> {    
}
