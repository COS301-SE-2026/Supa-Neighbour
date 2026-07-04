package com.app.api.services;

import com.app.api.dtos.LeaderboardEntry;
import com.app.api.dtos.LeaderboardResponse;
import com.app.api.repositories.LeaderboardRepository;
import org.springframework.stereotype.Service;
 
import java.util.List;

@Service
public class LeaderboardService {
    private final LeaderboardRepository  LeaderboardRepository;

    public LeaderboardService(LeaderboardRepository leaderboardRepository){
        this.LeaderboardRepository = leaderboardRepository;
    }

    /**
     * @param currentUserId  resolved from the JWT in the controller
     * @param limit          how many top entries to return (default 10)
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
