package com.app.api.unit.services;

import com.app.api.dtos.AchievementDTO;
import com.app.api.dtos.AchievementResponse;
import com.app.api.repositories.AchievementRepository;
import com.app.api.services.AchievementService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AchievementServiceTest {
    @Mock
    private AchievementRepository achievementRepository;

    @InjectMocks
    private AchievementService achievementService;

    private static final int USER_ID = 42;

    @Test
    void getAchievements_mapsEarnedRows_intoEarnedDTOs(){

        Object[] earned = new Object[]{1, "Helping Hand", "Completed 5 tasks", "2026-06-01"};

        List<Object[]> earnedList = List.<Object[]>of(earned);
        List<Object[]> emptyList = List.<Object[]>of();

        when(achievementRepository.findEarned(USER_ID)).thenReturn(earnedList);
        when(achievementRepository.findUnearned(USER_ID)).thenReturn(List.of());

        AchievementResponse response = achievementService.getAchievements(USER_ID);

        assertThat(response.getEarned()).hasSize(1);
        AchievementDTO dto = response.getEarned().get(0);

        assertThat(dto.getBadgeId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Helping Hand");
        assertThat(dto.getDescription()).isEqualTo("Completed 5 tasks");
        assertThat(dto.getAwardedOn()).isEqualTo("2026-06-01");
        assertThat(dto.getProgress()).isNull();
    }

    @Test
    void getAchievements_earnedRowWithNullAwardedOn_mapsToNull(){
        Object[] earnedRow = new Object []{ 1, "Helping Hand", "Completed 5 tasks", null};
        List<Object[]> earnedList = List.<Object[]>of(earnedRow);

        when(achievementRepository.findEarned(USER_ID)).thenReturn(earnedList);//

        AchievementResponse response = achievementService.getAchievements(USER_ID);

        assertThat(response.getEarned().get(0).getAwardedOn()).isNull();
    }

    @Test
    void getAchievements_mapsUnearnedRows_intoUnearnedDTOsWithProgress(){
        Object[] unearnedRow = new Object[] { 2, "Neighbourhood Hero","Completed 5 tasks", 18, 25};
        
        List<Object[]> unearnedList = List.<Object[]>of(unearnedRow);
        when(achievementRepository.findEarned(USER_ID)).thenReturn(List.of());
        when(achievementRepository.findUnearned(USER_ID)).thenReturn(unearnedList);//

        AchievementResponse response = achievementService.getAchievements(USER_ID);

        assertThat(response.getUnearned()).hasSize(1);
        AchievementDTO dto = response.getUnearned().get(0);
        assertThat(dto.getBadgeId()).isEqualTo(2);
        assertThat(dto.getName()).isEqualTo("Neighbourhood Hero");
        assertThat(dto.getDescription()).isEqualTo("Completed 5 tasks");
        assertThat(dto.getProgress()).isEqualTo("18/25");
        assertThat(dto.getAwardedOn()).isNull();
    }

    @Test
    void getAchievements_noRows_returnsEmptyEarnedAndUnearned(){
        when(achievementRepository.findEarned(USER_ID)).thenReturn(List.of());
        when(achievementRepository.findUnearned(USER_ID)).thenReturn(List.of());

        AchievementResponse response = achievementService.getAchievements(USER_ID);

        assertThat(response.getEarned()).isEmpty();
        assertThat(response.getUnearned()).isEmpty();
    }

    @Test
    void getAchievements_multipleRowsOfEachType_preservesOrderAndCount(){
        Object[] earned1 = new Object[] {1, "Helping Hand", "desc1", "2026-06-01"};
        Object[] earned2 = new Object[] {3, "Streak Master", "desc2", "2026-06-15"};
        Object[] unearned1 = new Object[] {2, "Neighbouthood Hero", "desc3", 13, 25};
        List<Object[]> unearnedList = List.<Object[]>of(unearned1);

        when(achievementRepository.findEarned(USER_ID)).thenReturn(List.of(earned1, earned2));
        when(achievementRepository.findUnearned(USER_ID)).thenReturn(unearnedList);//
        AchievementResponse response = achievementService.getAchievements(USER_ID);

        assertThat(response.getEarned()).extracting(AchievementDTO::getBadgeId).containsExactly(1, 3);
        assertThat(response.getUnearned()).extracting(AchievementDTO::getBadgeId).contains(2);

        
    }
}
