package com.app.api.services;

import com.app.api.dtos.UserStatusResponse;
import com.app.api.models.Settings;
import com.app.api.repositories.AchievementRepository;
import com.app.api.repositories.HelperRepository;
import com.app.api.repositories.HelperSkillRepository;
import com.app.api.repositories.LocationRepository;
import com.app.api.repositories.SettingsRepository;
import com.app.api.repositories.TaskRepository;
import com.app.api.repositories.UserAchievementRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.dtos.ShowStatusResponse;
import com.app.api.dtos.UserSettingsDTO;
import com.app.api.dtos.UpdateSettingsDTO;
import com.app.api.dtos.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.app.api.models.User;
import com.app.api.models.UserAchievement;
import com.app.api.dtos.AchievementDTO;
import com.app.api.dtos.AddressDTO;
import com.app.api.dtos.ModeResponse;
import java.time.Instant;
import com.app.api.dtos.RecentTaskDTO;
import com.app.api.models.Task;
import java.time.Duration;
import com.app.api.models.Address;
import com.app.api.models.HelperSkill;
import com.app.api.dtos.AddressInfoDTO;
import com.app.api.models.Location;
import com.app.api.models.Helper;
import java.util.List;

import com.app.api.services.RatingService;
import com.app.api.services.HelperTasksService;
import com.app.api.services.AchievementService;


/**
 * Service responsible for managing user application settings.
 * <p>
 * Provides functionality for retrieving and updating user preferences,
 * including online status visibility and application theme mode.
 * </p>
 */
@Service
public class SettingsServices {



    /**
     * Constructs a new {@code SettingsServices}.
     *
     * @param settingsRepository repository used to access and update
     *                           user settings
     */
    private final SettingsRepository settingsRepository;
    private final UserRepository userRepository;
    private final HelperRepository helperRepository;
    private final RatingService ratingService;
    private final HelperTasksService helperTasksService;
    private final AchievementService achievementService;

    public SettingsServices(
            SettingsRepository settingsRepository,
            UserRepository userRepository,
            HelperRepository helperRepository,
            RatingService ratingService,
            HelperTasksService helperTasksService,
            AchievementService achievementService) {

        this.settingsRepository = settingsRepository;
        this.userRepository = userRepository;
        this.helperRepository = helperRepository;
        this.ratingService = ratingService;
        this.helperTasksService = helperTasksService;
        this.achievementService = achievementService;
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
    public UserStatusResponse getUserStatus(int userId) {
        Settings settings = settingsRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found"));

        if (!settings.getShowStatus()) {
            return new UserStatusResponse(false);
        }

        Instant lastSeen = settings.getLastSeen();

        boolean online = lastSeen != null && Duration.between(lastSeen, Instant.now()).toMinutes() < 10;

        return new UserStatusResponse(true, online, lastSeen);
    }

    /**
     * Updates whether a user's online status is visible to others.
     *
     * @param userId     the unique identifier of the user
     * @param showStatus the new visibility setting
     * @return a response containing the updated visibility setting
     * @throws ResponseStatusException if the supplied value is invalid
     *                                 or the user's settings cannot
     *                                 be found
     */
    public ShowStatusResponse updateShowStatus(int userId, Boolean showStatus) {
        if (showStatus == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid showStatus value");
        }

        Settings settings = settingsRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found"));
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
    public ModeResponse getUserMode(int userId) {
        Settings settings = settingsRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "settings not found"));

        String mode = settings.getMode().name().toLowerCase();

        return new ModeResponse(mode);
    }


    private String calculateLevel(int xp){
        if(xp>=1000) return "Master";
        if(xp>=500) return "Expert";
        if(xp>=200) return "Skilled";
        if(xp>=50) return "Novice";
        return "Beginner";
    }
    /**
     * Updates the user's preferred application theme mode.
     * <p>
     * The supplied mode is validated against the supported
     * {@link Settings.ThemeMode} enumeration before being persisted.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @param mode   the new theme mode
     * @return a response containing the updated theme mode
     * @throws ResponseStatusException if the supplied mode is invalid
     *                                 or the user's settings cannot
     *                                 be found
     */
    public ModeResponse setUserMode(int userId, String mode) {
        if (mode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mode value");
        }

        Settings.ThemeMode parsedMode;
        try {
            parsedMode = Settings.ThemeMode.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mode value");
        }
        Settings settings = settingsRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "settings not found"));
        settings.setMode(parsedMode);
        settingsRepository.save(settings);
        return new ModeResponse(settings.getMode().name().toLowerCase());
    }

    /**
     * Retrieves address and settings information for a user.
     *
     * @param userId the unique identifier of the user
     * @return the user's address and settings information
     * @throws ResponseStatusException if the user or their settings cannot be found
     */
    public UserSettingsDTO getUserInfo(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Helper helper = helperRepository.findByUserid_Userid(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Helper not found"));

        Address address= user.getAddressid();
        AddressDTO addressDTO = new AddressDTO(address.getAddressid(),address.getNeighbourhoodid());

        Double trustScore = ratingService.getAverageRating(helper.getHelperid());
        String level = calculateLevel(helper.getHelperXp());
        Integer xp = helper.getHelperXp();

        List<RecentTaskDTO> recentTaskDTOs = helperTasksService.getTasks(userId, "completed", 5, 0)
            .getTasks()
            .stream()
            .map(t -> new RecentTaskDTO(t.getTaskId(), t.getTaskType(), t.getEndDate()))
            .toList();

        List<AchievementDTO> achievementDTOs = achievementService.getAchievements(userId).getEarned();
        UserProfileResponse responseProfile = new UserProfileResponse(
            user.getUserid(),
            user.getUsername(), 
            address.getNeighbourhoodid().getNeighbourhoodName(), 
            level, 
            xp, 
            List.of(helper.getTaskTypeid().getDescription()),
            recentTaskDTOs.size(),
            recentTaskDTOs,
            achievementDTOs,
            trustScore
        );

        return new UserSettingsDTO(responseProfile, addressDTO);
    }

    public List<UserProfileResponse> getAllUserProfiles(int userId) {
        return userRepository.findAll().stream().map(user -> {
            Address address = user.getAddressid();

            String neighbourhood = address.getNeighbourhoodid().getNeighbourhoodName();

            if (address != null && address.getNeighbourhoodid() != null) {
                neighbourhood = address.getNeighbourhoodid().getNeighbourhoodName();
            }

            return new UserProfileResponse(user.getUserid(), user.getUsername(), neighbourhood, null, null, null, 0,
                    null, null, null);
        }).toList();
    }

    public UserSettingsDTO updateSettings(int userId, UpdateSettingsDTO  dto)
    {
        Settings settings = settingsRepository.findById(userId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Settings not found"));
        
        if(dto.getShowStatus() != null){
           settings.setShowStatus(dto.getShowStatus());
        }

        if(dto.getShowPhoneNo() != null){
                settings.setShowPhoneNo(dto.getShowPhoneNo());       
        }

        if(dto.getMode() != null){
            settings.setMode(Settings.ThemeMode.valueOf(dto.getMode().toUpperCase()));
        }

        settingsRepository.save(settings);

        return getUserInfo(userId);
    }

    public void deleteUser(int userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"));

        userRepository.delete(user);
    }
}
