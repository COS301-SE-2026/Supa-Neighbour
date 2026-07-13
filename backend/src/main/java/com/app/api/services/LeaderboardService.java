package com.app.api.services;

import com.app.api.dtos.LeaderboardEntry;
import com.app.api.dtos.LeaderboardResponse;
import com.app.api.repositories.LeaderboardRepository;
import org.springframework.stereotype.Service;
 
import java.util.List;

/**
 * Service responsible for retrieving leaderboard information
 * for a user's neighbourhood.
 */
@Service
public class LeaderboardService {
    private final LeaderboardRepository  LeaderboardRepository;

     /**
     * Constructs a {@code LeaderboardService} with the required repository.
     *
     * @param leaderboardRepository repository used to retrieve leaderboard
     *                              and neighbourhood data
     */
    public LeaderboardService(LeaderboardRepository leaderboardRepository){
        this.LeaderboardRepository = leaderboardRepository;
    }

    /**
     * Retrieves the leaderboard for the authenticated user's neighbourhood.
     *
     * <p>The leaderboard is ranked by average helper rating. The response
     * includes the top-ranked helpers within the user's neighbourhood as
     * well as the authenticated user's own leaderboard entry, if present.</p>
     *
     * @param currentUserId the identifier of the authenticated user
     * @param limit the maximum number of leaderboard entries to return
     * @return a {@link LeaderboardResponse} containing the neighbourhood
     *         leaderboard and the authenticated user's ranking
     */
    public LeaderboardResponse getLeaderboard(int currentUserId, int limit){
        Object[] neighbourhood = LeaderboardRepository.findNeighbourhoodForUser(currentUserId);
        int neighbourhoodId = ((Number) neighbourhood[0]).intValue();
        String neighbourhoodName = (String) neighbourhood[1];

        List<LeaderboardEntry> allRanked = LeaderboardRepository.findRankedHelpersByNeighbourhood(neighbourhoodId);
        List<LeaderboardEntry> topN = allRanked.stream().limit(limit).toList();

        LeaderboardEntry currentUserEntry = allRanked.stream().filter(e -> e.getUserId() == currentUserId).findFirst().orElse(null);

        return new LeaderboardResponse(neighbourhoodName, "averageRating", topN, currentUserEntry);
    }
}
