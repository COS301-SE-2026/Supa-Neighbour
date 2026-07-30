package com.app.api.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import com.app.api.models.Badges;
import java.util.List;
import java.util.Optional;
import com.app.api.models.Ratings;
import com.app.api.repositories.BadgesRepository;
import com.app.api.services.BadgesService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BadgeServicesTest {

    @Mock
    private BadgesRepository badgesRepository;

    @InjectMocks
    private BadgesService badgesService;

    private Badges badges;

    @BeforeEach
    void setUp() {
        badges = new Badges();
        badges.setBadgeid(1);
        badges.setBadgeDescription("badge");
        badges.setBadgeName("swimmer");
        badges.setBadge_description("can swim well");
        badges.setIsSpecialist(true);
        badges.setRatingid(new Ratings());
        badges.setXpReward(50);
    }

    @Test
    void getAllBadges_returnList() {
        when(badgesRepository.findAll()).thenReturn(List.of(badges));
        List<Badges> result = badgesService.getAllBadges();

        assertEquals(1, result.size());

        verify(badgesRepository).findAll();
    }

    @Test
    void getBadgeByID_returnBadge() {
        when(badgesRepository.findById(1)).thenReturn(Optional.of(badges));

        Badges result = badgesService.getBadgesById(1);

        assertNotNull(result);
        assertEquals(badges, result);

        verify(badgesRepository).findById(1);
    }

    @Test
    void saveBadges_validBadges_returnSavedBadge() {
        when(badgesRepository.save(badges)).thenReturn(badges);

        Badges result = badgesService.saveBadges(badges);

        assertNotNull(result);
        assertEquals(badges, result);

        verify(badgesRepository).save(badges);
    }

    @Test
    void updateBadge_returnBadge() {
        Badges updated = new Badges();

        updated.setBadgeid(1);
        updated.setBadgeName("Expert Helper");
        updated.setBadgeDescription("Updated Description");
        updated.setBadge_description("Updated Description");
        updated.setXpReward(500);
        updated.setRatingid(new Ratings());
        updated.setIsSpecialist(false);

        when(badgesRepository.findById(1)).thenReturn(Optional.of(badges));
        when(badgesRepository.save(any(Badges.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Badges result = badgesService.updateBadges(1, updated);

        assertNotNull(result);
        assertEquals(updated.getBadgeid(), result.getBadgeid());
        assertEquals(updated.getBadgeDescription(), result.getBadgeDescription());
        assertEquals(updated.getBadgeName(), result.getBadgeName());
        assertEquals(updated.getIsSpecialist(), result.getIsSpecialist());
        assertEquals(updated.getRatingid(), result.getRatingid());
        assertEquals(updated.getXpReward(), result.getXpReward());

        verify(badgesRepository).findById(1);
        verify(badgesRepository).save(badges);
    }

    @Test
    void deleteBadge_withValidBadge() {
        doNothing().when(badgesRepository).deleteById(1);

        badgesService.deleteBadges(1);

        verify(badgesRepository).deleteById(1);
    }
}
