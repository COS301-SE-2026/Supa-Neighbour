package com.app.api.repositories;

import com.app.api.models.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, Integer>{
    
}
