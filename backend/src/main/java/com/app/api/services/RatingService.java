package com.app.api.services;
 
import com.app.api.dtos.RatingRequest;
import com.app.api.dtos.RatingResponse;
import com.app.api.repositories.RatingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
public class RatingService{

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository){
        this.ratingRepository = ratingRepository;
    }

    @Transactional
    public RatingResponse submitRating(int taskId, int callerId, RatingRequest request){
        String userType = ratingRepository.findUserType(callerId);
        if(userType == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if(userType.equals("Admin")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins are not permitted to submit ratings");
        }

        Object[] task = ratingRepository.findTaskById(taskId);
        if(task == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }

        int helperId = ((Number) task[1]).intValue();
        String existingRating = (String) task[2];
        String status = (String) task[3];
        if (!status.equals("completed")) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Task must be completed before it can be rated");
        }

        if (existingRating != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already submitted a rating for this task");
        }

        Integer dependentUserId = ratingRepository.findDependentUserId(taskId);

        if(dependentUserId  == null || !dependentUserId.equals(callerId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorised to rate task");
        }

        if(!ratingRepository.isValidRating(request.getRating())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rating must be one of: Outstanding, Excellent, Very Good, Average");
        }

        ratingRepository.recalculateAverageRating(helperId);
 
        return new RatingResponse(
                "Rating submitted successfully.",
                taskId,
                request.getRating(),
                request.getReviewSnippet()
        );

    }
}