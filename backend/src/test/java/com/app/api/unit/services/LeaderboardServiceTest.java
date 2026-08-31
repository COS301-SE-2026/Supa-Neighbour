package com.app.api.unit.services;

import com.app.api.dtos.LeaderboardEntry;
import com.app.api.dtos.LeaderboardResponse;
import com.app.api.repositories.LeaderboardRepository;
import com.app.api.services.LeaderboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private LeaderboardRepository leaderboardRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;


    @Test
    void getLeaderboard_WhenCalled_ReturnsNeighbourhoodName() {
        Object[] neighbourhood = new Object[]{2, "Hillcrest"};
        when(leaderboardRepository.findNeighbourhoodForUser(1)).thenReturn(neighbourhood);
        when(leaderboardRepository.findRankedHelpersByNeighbourhood(2)).thenReturn(List.of());

        LeaderboardResponse response = leaderboardService.getLeaderboard(1, 10);

        assertEquals("Hillcrest", response.getNeighbourhood());
    }

    @Test
    void getLeaderboard_WhenCalled_RankByIsAlwaysAverageRating() {
        Object[] neighbourhood = new Object[]{2, "Hillcrest"};
        when(leaderboardRepository.findNeighbourhoodForUser(1)).thenReturn(neighbourhood);
        when(leaderboardRepository.findRankedHelpersByNeighbourhood(2)).thenReturn(List.of());

        LeaderboardResponse response = leaderboardService.getLeaderboard(1, 10);

        assertEquals("averageRating", response.getRankBy());
    }

    @Test
    void getLeaderboard_WhenLimitIsLessThanTotal_ReturnsOnlyTopN() {
        Object[] neighbourhood = new Object[]{2, "Hillcrest"};
        List<LeaderboardEntry> allEntries = List.of(
                new LeaderboardEntry(1, 10, "Alice A.", 5.0, 1),
                new LeaderboardEntry(2, 11, "Bob B.",   4.5, 2),
                new LeaderboardEntry(3, 12, "Carol C.", 4.0, 3)
        );
        when(leaderboardRepository.findNeighbourhoodForUser(1)).thenReturn(neighbourhood);
        when(leaderboardRepository.findRankedHelpersByNeighbourhood(2)).thenReturn(allEntries);

        LeaderboardResponse response = leaderboardService.getLeaderboard(1, 2);

        assertEquals(2, response.getLeaderboard().size());
        assertEquals(10, response.getLeaderboard().get(0).getUserId());
        assertEquals(11, response.getLeaderboard().get(1).getUserId());
    }

    @Test
    void getLeaderboard_WhenLimitExceedsTotal_ReturnsAllEntries() {
        Object[] neighbourhood = new Object[]{2, "Hillcrest"};
        List<LeaderboardEntry> allEntries = List.of(
                new LeaderboardEntry(1, 10, "Alice A.", 5.0, 1),
                new LeaderboardEntry(2, 11, "Bob B.",   4.5, 2)
        );
        when(leaderboardRepository.findNeighbourhoodForUser(1)).thenReturn(neighbourhood);
        when(leaderboardRepository.findRankedHelpersByNeighbourhood(2)).thenReturn(allEntries);

        LeaderboardResponse response = leaderboardService.getLeaderboard(1, 10);

        assertEquals(2, response.getLeaderboard().size());
    }

    @Test
    void getLeaderboard_WhenCurrentUserIsOnLeaderboard_SetsCurrentUserEntry() {
        Object[] neighbourhood = new Object[]{2, "Hillcrest"};
        LeaderboardEntry userEntry = new LeaderboardEntry(2, 1, "John S.", 4.5, 5);
        List<LeaderboardEntry> allEntries = List.of(
                new LeaderboardEntry(1, 99, "Top T.", 5.0, 3),
                userEntry
        );
        when(leaderboardRepository.findNeighbourhoodForUser(1)).thenReturn(neighbourhood);
        when(leaderboardRepository.findRankedHelpersByNeighbourhood(2)).thenReturn(allEntries);

        LeaderboardResponse response = leaderboardService.getLeaderboard(1, 10);

        assertNotNull(response.getCurrentUser());
        assertEquals(1, response.getCurrentUser().getUserId());
        assertEquals(2, response.getCurrentUser().getRank());
    }

    @Test
    void getLeaderboard_WhenCurrentUserIsNotOnLeaderboard_CurrentUserIsNull() {
        Object[] neighbourhood = new Object[]{2, "Hillcrest"};
        List<LeaderboardEntry> allEntries = List.of(
                new LeaderboardEntry(1, 99, "Top T.", 5.0, 3)
        );
        when(leaderboardRepository.findNeighbourhoodForUser(1)).thenReturn(neighbourhood);
        when(leaderboardRepository.findRankedHelpersByNeighbourhood(2)).thenReturn(allEntries);

        LeaderboardResponse response = leaderboardService.getLeaderboard(1, 10);

        assertNull(response.getCurrentUser());
    }

    @Test
    void getLeaderboard_WhenNoHelpersInNeighbourhood_ReturnsEmptyLeaderboard() {
        Object[] neighbourhood = new Object[]{2, "Hillcrest"};
        when(leaderboardRepository.findNeighbourhoodForUser(1)).thenReturn(neighbourhood);
        when(leaderboardRepository.findRankedHelpersByNeighbourhood(2)).thenReturn(List.of());

        LeaderboardResponse response = leaderboardService.getLeaderboard(1, 10);

        assertTrue(response.getLeaderboard().isEmpty());
        assertNull(response.getCurrentUser());
    }

    @Test
    void getLeaderboard_WhenCurrentUserIsRank1_LevelIsGold() {
        Object[] neighbourhood = new Object[]{2, "Hillcrest"};
        LeaderboardEntry userEntry = new LeaderboardEntry(1, 1, "John S.", 5.0, 5);
        when(leaderboardRepository.findNeighbourhoodForUser(1)).thenReturn(neighbourhood);
        when(leaderboardRepository.findRankedHelpersByNeighbourhood(2)).thenReturn(List.of(userEntry));

        LeaderboardResponse response = leaderboardService.getLeaderboard(1, 10);

        assertEquals("Gold", response.getCurrentUser().getLevel());
    }

    @Test
    void getLeaderboard_WhenCurrentUserIsRank4_LevelIsNull() {
        Object[] neighbourhood = new Object[]{2, "Hillcrest"};
        LeaderboardEntry userEntry = new LeaderboardEntry(4, 1, "John S.", 3.0, 5);
        when(leaderboardRepository.findNeighbourhoodForUser(1)).thenReturn(neighbourhood);
        when(leaderboardRepository.findRankedHelpersByNeighbourhood(2)).thenReturn(List.of(userEntry));

        LeaderboardResponse response = leaderboardService.getLeaderboard(1, 10);

        assertNull(response.getCurrentUser().getLevel());
    }

    @Test
    void getLeaderboard_UsesCorrectNeighbourhoodIdForRanking() {
        Object[] neighbourhood = new Object[]{7, "Arcadia"};
        when(leaderboardRepository.findNeighbourhoodForUser(1)).thenReturn(neighbourhood);
        when(leaderboardRepository.findRankedHelpersByNeighbourhood(7)).thenReturn(List.of());

        leaderboardService.getLeaderboard(1, 10);

        verify(leaderboardRepository).findRankedHelpersByNeighbourhood(7);
    }
}
