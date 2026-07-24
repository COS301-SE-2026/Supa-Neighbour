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
public class BulletinServicesTest {
    
}
