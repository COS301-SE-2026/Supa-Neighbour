package com.app.api.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.api.dtos.AchievementDTO;
import com.app.api.dtos.RecentTaskDTO;
import com.app.api.dtos.UpdateProfileRequest;
import com.app.api.dtos.UpdateProfileResponse;
import com.app.api.dtos.UserProfileResponse;
import com.app.api.models.Helper;
import com.app.api.models.HelperSkill;
import com.app.api.models.TaskType;
import com.app.api.models.User;
import com.app.api.repositories.HelperRepository;
import com.app.api.repositories.HelperSkillRepository;
import com.app.api.repositories.TaskTypeRepository;
import com.app.api.repositories.UserProfileRepository;
import com.app.api.repositories.UserRepository;

import jakarta.transaction.Transactional;

/**
 * Service responsible for retrieving and updating user profile
 * information, including helper-specific details such as skills,
 * achievements, and recent tasks.
 */
@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final HelperRepository helperRepository;
    private final HelperSkillRepository helperSkillRepository;
    private final TaskTypeRepository taskTypeRepository;

     /**
     * Constructs a {@code UserProfileService} with the required repositories.
     *
     * @param userProfileRepository repository used to retrieve profile data
     * @param userRepository repository used to access and update user information
     * @param helperRepository repository used to retrieve helper records
     * @param helperSkillRepository repository used to manage helper skills
     * @param taskTypeRepository repository used to validate available task types
     */
    public UserProfileService(UserProfileRepository userProfileRepository,
                              UserRepository userRepository,
                              HelperRepository helperRepository,
                              HelperSkillRepository helperSkillRepository,
                              TaskTypeRepository taskTypeRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.helperRepository = helperRepository;
        this.helperSkillRepository = helperSkillRepository;
        this.taskTypeRepository = taskTypeRepository;
    }

    /**
     * Retrieves the profile of the specified user.
     *
     * <p>The returned profile includes general user information and,
     * if the user is registered as a helper, helper-specific details
     * such as level, experience points, skills, completed tasks,
     * recent tasks, and earned achievements.</p>
     *
     * @param userId the identifier of the user whose profile is requested
     * @return a {@link UserProfileResponse} containing the user's profile
     *         information
     * @throws ResponseStatusException if the user cannot be found
     */

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

        return new UserProfileResponse(resolvedUserId, displayName, neighbourhood, level, currentXp, skills, completedTasks, recentTasks, achievements,trustScore);
    }

    /**
     * Updates the authenticated user's profile information.
     *
     * <p>The user's first name, last name, and helper skills may be
     * updated. If skills are supplied, they are validated against the
     * available task types before replacing the helper's existing
     * skills.</p>
     *
     * @param userId the identifier of the user whose profile is being updated
     * @param request the requested profile changes
     * @return an {@link UpdateProfileResponse} describing the updated profile
     * @throws ResponseStatusException if no update fields are supplied,
     *         the user or helper cannot be found, or one or more
     *         requested skills are invalid
     */
    @Transactional
    public UpdateProfileResponse updateProfile(int userId, UpdateProfileRequest request){
        if(request.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Atleast one of firstname, lastName, or skills must be provided");

        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usr not founr"));

        boolean changedPersonName = false;

        if(request.getFirstName() != null){
            user.setFirstName(request.getFirstName());
            changedPersonName = true;
        }

        if(request.getLastName() != null){
            user.setLastName(request.getLastName());
            changedPersonName = true;
        }

        if(changedPersonName){
            userRepository.save(user);
        }

        List<String> updatedSkills = null;
        if(request.getSkills() != null){
           
            Helper helper = helperRepository.findByUserid_Userid(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "User is not registered as helper"));
            List<TaskType> matchedTypes = taskTypeRepository.findByDescriptionIn(request.getSkills());
            System.out.println("DEBUG: entering updateProfile, userId=" + matchedTypes.toString());
            if(matchedTypes.size() != request.getSkills().size()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more requested skills are invalid");
            }
             
            
            helperSkillRepository.deleteHelperId(helper.getHelperid());
            System.out.println("DEBUG: delete done");
            List<HelperSkill> newSkills = matchedTypes.stream()
                    .map(taskType -> {
                        HelperSkill hs = new HelperSkill();
                        hs.setHelperId(helper);
                        hs.setTaskTypeId(taskType);
                        return hs;
                    })
                    .collect(Collectors.toList());

            helperSkillRepository.saveAll(newSkills);
            System.out.println("DEBUG: saveAll done");

            updatedSkills = matchedTypes.stream().map(TaskType::getDescription).collect(Collectors.toList());
            System.out.println("DEBUG: updatedSkills done");
            
        }

        String displayName = user.getFirstName();
        if(user.getLastName() != null && !user.getLastName().isBlank()){
            displayName += " " + user.getLastName().charAt(0) + ".";
        }

        return new UpdateProfileResponse("Profile updated", displayName, updatedSkills);
    }

}
