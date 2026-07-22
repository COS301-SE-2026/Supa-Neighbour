package com.app.api.services;

import com.app.api.dtos.UserStatusResponse;
import com.app.api.models.Settings;
import com.app.api.repositories.SettingsRepository;
import com.app.api.dtos.ShowStatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.api.dtos.ModeResponse;

import java.time.Duration;
import java.time.Instant;
/**
 * Service responsible for managing user application settings.
 * <p>
 * Provides functionality for retrieving and updating user preferences,
 * including online status visibility and application theme mode.
 * </p>
 */
@Service
public class SettingsServices {
    
    private SettingsRepository settingsRepository;

    /**
     * Constructs a new {@code SettingsServices}.
     *
     * @param settingsRepository repository used to access and update
     *                           user settings
     */
    public SettingsServices(SettingsRepository settingsRepository){
        this.settingsRepository = settingsRepository;
    }

    /**
     * Retrieves the online status information for a user.
     * <p>
     * If the user has disabled status visibility, only the visibility
     * setting is returned. Otherwise, the user's online status is
     * determined by whether their last recorded activity occurred
     * within the previous ten minutes.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @return a response containing the user's visibility setting,
     *         online status, and last seen timestamp
     * @throws ResponseStatusException if no settings record exists
     *                                 for the specified user
     */
    public UserStatusResponse getUserStatus(int userId){
        Settings settings = settingsRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found"));

        if(!settings.getShowStatus()){
            return new UserStatusResponse(false);
        }

        Instant lastSeen = settings.getLastSeen();

        boolean online = lastSeen != null && Duration.between(lastSeen, Instant.now()).toMinutes() < 10;

        return new UserStatusResponse(true, online, lastSeen);
    }

     /**
     * Updates whether a user's online status is visible to others.
     *
     * @param userId the unique identifier of the user
     * @param showStatus the new visibility setting
     * @return a response containing the updated visibility setting
     * @throws ResponseStatusException if the supplied value is invalid
     *                                 or the user's settings cannot
     *                                 be found
     */
    public ShowStatusResponse updateShowStatus(int userId, Boolean showStatus){
        if(showStatus == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid showStatus value");
        }

        Settings settings = settingsRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found"));
        settings.setShowStatus(showStatus);
        settingsRepository.save(settings);

        return new ShowStatusResponse(settings.getShowStatus());
    }

    /**
     * Retrieves the user's preferred application theme mode.
     *
     * @param userId the unique identifier of the user
     * @return a response containing the user's current theme mode
     * @throws ResponseStatusException if the user's settings cannot
     *                                 be found
     */
    public ModeResponse getUserMode(int userId){
        Settings settings = settingsRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "settings not found"));

        String mode = settings.getMode().name().toLowerCase();

        return new ModeResponse(mode);
    }

    /**
     * Updates the user's preferred application theme mode.
     * <p>
     * The supplied mode is validated against the supported
     * {@link Settings.ThemeMode} enumeration before being persisted.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @param mode the new theme mode
     * @return a response containing the updated theme mode
     * @throws ResponseStatusException if the supplied mode is invalid
     *                                 or the user's settings cannot
     *                                 be found
     */
    public ModeResponse setUserMode(int userId, String mode){
        if(mode == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mode value");
        }

        Settings.ThemeMode parsedMode;
        try{
            parsedMode = Settings.ThemeMode.valueOf(mode.toUpperCase());
        }catch(IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mode value");
        }
        Settings settings = settingsRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "settings not found"));
        settings.setMode(parsedMode);
        settingsRepository.save(settings);
        return new ModeResponse(settings.getMode().name().toLowerCase());
    }
}
