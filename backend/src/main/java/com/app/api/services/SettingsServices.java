package com.app.api.services;

import com.app.api.dtos.UserStatusResponse;
import com.app.api.models.Settings;
import com.app.api.repositories.SettingsRepository;
import com.app.api.dtos.ShowStatusRequest;
import com.app.api.dtos.ShowStatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;

@Service
public class SettingsServices {
    
    private SettingsRepository settingsRepository;

    public SettingsServices(SettingsRepository settingsRepository){
        this.settingsRepository = settingsRepository;
    }

    public UserStatusResponse getUserStatus(int userId){
        Settings settings = settingsRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found"));

        if(!settings.getShowStatus()){
            return new UserStatusResponse(false);
        }

        Instant lastSeen = settings.getLastSeen();

        boolean online = lastSeen != null && Duration.between(lastSeen, Instant.now()).toMinutes() < 10;

        return new UserStatusResponse(true, online, lastSeen);
    }

    public ShowStatusResponse updateShowStatus(int userId, Boolean showStatus){
        if(showStatus == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid showStatus value");
        }

        Settings settings = settingsRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found"));
        settings.setShowStatus(showStatus);
        settingsRepository.save(settings);

        return new ShowStatusResponse(settings.getShowStatus());
    }
}
