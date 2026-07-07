package com.app.api.services;
import com.app.api.dtos.HelperProfileResponse;
import com.app.api.dtos.ReviewDTO;
import com.app.api.repositories.HelperprofileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
 
import java.util.List;

@Service
public class HelperProfileService {
    
    private final HelperprofileRepository helperProfileRepository;

    public HelperProfileService(HelperprofileRepository helperProfileRepository){
        this.helperProfileRepository = helperProfileRepository;
    }

    public HelperProfileResponse getProfile(int helperId){
        Object[] core = helperProfileRepository.findHelperCore(helperId);

        if(core == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Helper not found");
        }

        String displayName = (String) core[1];
        double trustScore = ((Number) core[2]).doubleValue();
        int neighbourhoodId = ((Number) core[3]).intValue();

        int rank = helperProfileRepository.findHelperRank(helperId, neighbourhoodId);
        String level = switch (rank){
            case 1 -> "Gold";
            case 2 -> "Silver";
            case 3 -> "Bronze";
            default -> null;
        };

        int completedTasks = helperProfileRepository.CompletedTasks(helperId);
        int neighboursHelped = helperProfileRepository.countNeighboursHelped(helperId);
        List<String> skills = helperProfileRepository.findSkills(helperId);
        List<ReviewDTO> reviews = helperProfileRepository.findReviews(helperId).stream()
        .map(row -> new ReviewDTO
            ( (String) row[0], 
            (String) row[1],
             row[2] != null ? row[2].toString(): null))
            .toList();
        
            return new HelperProfileResponse(helperId, displayName, level, trustScore, completedTasks, neighboursHelped, skills, reviews);
    }
}
