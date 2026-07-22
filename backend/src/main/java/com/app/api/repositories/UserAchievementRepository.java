package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.app.api.models.User;
import com.app.api.models.UserAchievement;

/**
 * Repository for UserAchievement entities.
 */
@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Integer> {

}
