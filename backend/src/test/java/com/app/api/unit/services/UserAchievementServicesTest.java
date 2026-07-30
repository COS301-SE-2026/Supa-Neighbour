package com.app.api.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import com.app.api.models.UserAchievement;
import com.app.api.models.Badges;
import com.app.api.repositories.UserAchievementRepository;
import com.app.api.services.UserAchievementService;
import com.app.api.models.User;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserAchievementServicesTest {
    @Mock
    private UserAchievementRepository repository;

    @InjectMocks
    private UserAchievementService service;

    private UserAchievement achievement;

    @BeforeEach
    void setUp() {
        achievement = new UserAchievement();

        achievement.setAwardedOn(LocalDate.of(2025, 1, 1));
        achievement.setUserId(new User());
        achievement.setBadgeId(new Badges());
        achievement.setProgressCurrent(10);
        achievement.setProgressTarget(20);

    }

    @Test
    void getAllUserAchievements_returnList() {
        when(repository.findAll()).thenReturn(List.of(achievement));
        List<UserAchievement> result = service.getAllUserAchievement();

        assertEquals(1, result.size());

        verify(repository).findAll();
    }

    @Test
    void getAchievementById_returnAddress() {
        when(repository.findById(1)).thenReturn(Optional.of(achievement));

        UserAchievement result = service.getUserAchievementById(1);

        assertNotNull(result);
        assertEquals(achievement,result);

        verify(repository).findById(1);
    }

    @Test
    void saveAchievements_validAchievements_returnSavedAchievement() {
        when(repository.save(achievement)).thenReturn(achievement);

        UserAchievement result = service.saveAchievement(achievement);

        assertNotNull(result);
        assertEquals(achievement, result);

        verify(repository).save(achievement);
    }

    @Test
    void updateAchievement_returnAchievement() {
        UserAchievement updated = new UserAchievement();

        updated.setAwardedOn(LocalDate.of(2025, 1, 1));
        updated.setUserId(new User());
        updated.setBadgeId(new Badges());
        updated.setProgressCurrent(50);
        updated.setProgressTarget(100);

        when(repository.findById(1)).thenReturn(Optional.of(achievement));
        when(repository.save(any(UserAchievement.class)))
        .thenAnswer(i ->i.getArgument(0));

        UserAchievement result= service.updateUserAchievement(1, updated);

        assertNotNull(result);
        assertEquals(updated.getAwardedOn(), result.getAwardedOn());
        assertEquals(updated.getUserId(), result.getUserId());
        assertEquals(updated.getBadgeId(), result.getBadgeId());
        assertEquals(updated.getProgressCurrent(), result.getProgressCurrent());
        assertEquals(updated.getProgressTarget(), result.getProgressTarget());

        verify(repository).findById(1);
        verify(repository).save(achievement);
    }

    @Test
    void deleteUserAchievement_withValidAchievement() {
        doNothing().when(repository).deleteById(1);

        service.deleteUserAchievement(1);;

        verify(repository).deleteById(1);
    } 
}
