package com.app.api.services;

import com.app.api.dtos.AchievementDTO;
import com.app.api.dtos.RecentTaskDTO;
import com.app.api.dtos.UserProfileResponse;
import com.app.api.repositories.UserProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
 
import java.util.List;


@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository){
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfileResponse getProfile(int userId){
        Object[] core = userProfileRepository.findUserCore(userId);

        if(core == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        int resolvedUserId = ((Number) core[0]).intValue();
        String displayName = (String) core[1];
        String neighbourhood = (String) core[2];
        int neighbourhoodId = ((Number) core[3]).intValue();

        Object[] helperData = userProfileRepository.findHelperData(userId);

        Integer currentXp = null;
        Double trustScore = null;
        String level = null;
        List<String> skills = null;
        int completedTasks = 0;
        List<RecentTaskDTO> recentTasks = List.of();

        if(helperData != null){
            int helperId = ((Number) helperData[0]).intValue();
            currentXp = ((Number) helperData[1]).intValue();
            trustScore = ((Number) helperData[2]).doubleValue();

            int rank = userProfileRepository.findHelperRank(helperId, neighbourhoodId);
            level = switch (rank) {
                case 1  -> "Gold";
                case 2  -> "Silver";
                case 3  -> "Bronze";
                default -> null;
            };

            skills = userProfileRepository.findSkills(helperId);
            completedTasks = userProfileRepository.countCompletedTasks(helperId);


            recentTasks = userProfileRepository.findRecentTasks(helperId).stream()
            .map(row -> new RecentTaskDTO(
                ((Number) row[0]).intValue(),
                (String) row[1],
                row[2] != null ? row[2].toString() : null
            )).toList();
        }

        List<AchievementDTO> achievements = userProfileRepository.findEarnedAchievements(userId).stream()
        .map(row -> new AchievementDTO(
                ((Number) row[0]).intValue(),
                (String)  row[1],
                (String)  row[2],
                row[3] != null ? row[3].toString() : null

        )).toList();

        return new UserProfileResponse(resolvedUserId, displayName, neighbourhood, level, currentXp, skills, completedTasks, recentTasks, achievements);
    }

}
