package com.app.api.repositories;
import com.app.api.models.Settings;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, Integer>{

    /**
     * Finds the settings belonging to a user.
     *
     * @param userId the user's identifier
     * @return the user's settings if found
     */
    Optional<Settings> findByUserid_Userid(int userId);
}
