package com.app.api.unit.services;

import com.app.api.models.Badges;
import com.app.api.models.Ratings;
import com.app.api.repositories.BadgesRepository;
import com.app.api.services.BadgesService;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BadgesServiceTest {
    
    @Mock
    private BadgesRepository badgesRepository;

    private BadgesService badgesService;

    private Badges sampleBadge;
    private Ratings sampleRatings;

    @BeforeEach
    void setup(){
        badgesService = new BadgesService(badgesRepository);

        sampleRatings = new Ratings(3, "Excellent", 500, "Veteran");

        sampleBadge = new Badges();
        sampleBadge.setBadgeid(1);
        sampleBadge.setBadgeName("Helping Hand");
        sampleBadge.setBadgeDescription("Completed 5 tasks");
        sampleBadge.setXpReward(50);
        sampleBadge.setRatingid(sampleRatings);
        sampleBadge.setIsSpecialist(false);
    }

    @Test
    void getAllBadges_returnsAllFromRepository(){
        when(badgesRepository.findAll()).thenReturn(List.of(sampleBadge));
        List<Badges> result = badgesService.getAllBadges();

        assertThat(result).hasSize(1).containsExactly(sampleBadge);
    }

    @Test
    void getBadgesId_found_returnsBadge(){
        when(badgesRepository.findById(1)).thenReturn(Optional.of(sampleBadge));
        Badges result = badgesService.getBadgesById(1);

        assertThat(result).isEqualTo(sampleBadge);
    }

    @Test
    void getBadgesById_notFound_returnsNull(){
        when(badgesRepository.findById(99)).thenReturn(Optional.empty());

        Badges result = badgesService.getBadgesById(99);
        assertThat(result).isNull();
    }

    @Test
    void saveBadges_validBadge_savesAndReturns(){
        when(badgesRepository.save(sampleBadge)).thenReturn(sampleBadge);

        Badges result = badgesService.saveBadges(sampleBadge);

        assertThat(sampleBadge).isEqualTo(sampleBadge);
        verify(badgesRepository).save(sampleBadge);
    }

    @Test
    void saveBadges_nullBadge_returnsNullWithoutCallingRepository(){
        Badges result = badgesService.saveBadges(null);

        assertThat(result).isNull();
        verify(badgesRepository, never()).save(any(Badges.class));
    }

    @Test
    void updateBadges_existing_updatedAllFieldsAndSaves(){
        Ratings newRating = new Ratings(5, "Outstanding", 1000, "Elite");
        Badges updateData = new Badges();
        updateData.setBadgeName("Neighbourhood Hero");
        updateData.setBadgeDescription("Completed 25 tasks");
        updateData.setXpReward(200);
        updateData.setRatingid(newRating);
        updateData.setIsSpecialist(true);

        when(badgesRepository.findById(1)).thenReturn(Optional.of(sampleBadge));
        when(badgesRepository.save(sampleBadge)).thenReturn(sampleBadge);

        Badges result = badgesService.updateBadges(1, updateData);
        assertThat(result.getBadgeName()).isEqualTo("Neighbourhood Hero");
        assertThat(result.getBadgeDescription()).isEqualTo("Completed 25 tasks");
        assertThat(result.getXpReward()).isEqualTo(200);
        assertThat(result.getRatingid()).isEqualTo(newRating);
        assertThat(result.getIsSpecialist()).isTrue();
        verify(badgesRepository).save(sampleBadge);
    }   

    @Test
    void updatesBadges_notFount_returnsNullWithoutSaving (){
        when(badgesRepository.findById(99)).thenReturn(Optional.empty());

        Badges result = badgesService.updateBadges(99, sampleBadge);
        assertThat(result).isNull();
        verify(badgesRepository, never()).save(any(Badges.class));
    }

    @Test
    void deleteBadges_callsRepositoryDeleteById(){
        badgesService.deleteBadges(1);
        verify(badgesRepository).deleteById(1);
    }

}
