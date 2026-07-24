package com.app.api.unit.services;

import com.app.api.dtos.ShowStatusResponse;
import com.app.api.dtos.UserStatusResponse;
import com.app.api.models.Settings;
import com.app.api.repositories.SettingsRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.repositories.HelperRepository;
import com.app.api.services.HelperTasksService;
import com.app.api.services.AchievementService;
import com.app.api.services.RatingService;
import com.app.api.services.SettingsServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SettingsServicesTest {

    @Mock
    private SettingsRepository settingsRepository;

    private SettingsServices settingsServices;

    private Settings settings;

    private UserRepository userRepository;
    private HelperRepository helperRepository;
    private RatingService ratingService;
    private HelperTasksService helperTasksService;
    private AchievementService achievementService;

    @BeforeEach
    void setUp(){
        settingsServices = new SettingsServices(settingsRepository, userRepository, helperRepository, ratingService, helperTasksService, achievementService);
        settings = new Settings();
        settings.setShowStatus(true);
    }

    @Test
    void getUserStatus_whenShowStatusFalse_returnsVisisbleFlaseOnly(){
        settings.setShowStatus(false);
        when(settingsRepository.findById(1)).thenReturn(Optional.of(settings));

        UserStatusResponse response = settingsServices.getUserStatus(1);

        assertFalse(response.getVisible());
        assertNull(response.getOnline());
        assertNull(response.getLastSeen());
    }

    @Test
    void getUserStatus_whenLastSeenOver10MinutesAgo_returnsOnlineFalse(){
        settings.setShowStatus(true);
        settings.setLastSeen(Instant.now().minusSeconds(700));
        when(settingsRepository.findById(1)).thenReturn(Optional.of(settings));
        UserStatusResponse response = settingsServices.getUserStatus(1);

        assertTrue(response.getVisible());
        assertFalse(response.getOnline());
    }

    @Test
    void getUserStatus_whenLastSeenIsNull_returnsOnlineFalse(){
        settings.setShowStatus(true);
        settings.setLastSeen(null);
        when(settingsRepository.findById(1)).thenReturn(Optional.of(settings));

        UserStatusResponse response = settingsServices.getUserStatus(1);
        assertTrue(response.getVisible());
        assertFalse(response.getOnline());
    }

    @Test
    void getUserStatus_whenSettingsNotFound_throws404(){
        when(settingsRepository.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> settingsServices.getUserStatus(99)
        );

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void updateShowStatus_whenValidTrue_savesAndReturnsTrue(){
        when(settingsRepository.findById(1)).thenReturn(Optional.of(settings));

        ShowStatusResponse response = settingsServices.updateShowStatus(1, true);

        assertTrue(response.getShowStatus());
        verify(settingsRepository).save(settings);
        assertTrue(settings.getShowStatus());
    }

    @Test
    void updateShowStatus_whenValidFalse_savesAndReturnsFalse(){
        when(settingsRepository.findById(1)).thenReturn(Optional.of(settings));

        ShowStatusResponse response = settingsServices.updateShowStatus(1, false);
        assertFalse(response.getShowStatus());
        verify(settingsRepository).save(settings);
    }

    @Test
    void updateShowStatus_whenNull_throws400_andNeverSaves(){
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> settingsServices.updateShowStatus(1, null)
        );
        assertEquals(400, ex.getStatusCode().value());
        verify(settingsRepository, never()).save(any());
    }

    @Test
    void updateShowStatus_whenSettingsNotFounr_throws404(){
        when(settingsRepository.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> settingsServices.updateShowStatus(99, true)
        );

        assertEquals(404, ex.getStatusCode().value());
    }
}
