package com.app.api.unit.services;

import com.app.api.dtos.RatingRequest;
import com.app.api.dtos.RatingResponse;
import com.app.api.repositories.RatingRepository;
import com.app.api.services.RatingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    private RatingRequest buildRequest(Integer rating, String reviewSnippet) {
        RatingRequest request = new RatingRequest();
        request.setRating(rating);
        request.setReviewSnippet(reviewSnippet);
        return request;
    }

    @Test
    void submitRating_WhenTaskNotFound_ThrowsNotFound() {
        RatingRequest request = buildRequest(5, null);
        when(ratingRepository.findTaskById(3)).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ratingService.submitRating(3, 42, request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void submitRating_WhenNoDependentAssociatedWithTask_ThrowsForbidden() {
        RatingRequest request = buildRequest(5, null);
        Object[] task = new Object[] { 3, 7, null, "completed" };
        when(ratingRepository.findTaskById(3)).thenReturn(task);
        when(ratingRepository.findDependentUserId(3)).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ratingService.submitRating(3, 42, request));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void submitRating_WhenCallerIsNotTheDependent_ThrowsForbidden() {
        RatingRequest request = buildRequest(5, null);
        Object[] task = new Object[] { 3, 7, null, "completed" };
        when(ratingRepository.findTaskById(3)).thenReturn(task);
        when(ratingRepository.findDependentUserId(3)).thenReturn(99);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ratingService.submitRating(3, 42, request));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void submitRating_WhenRatingValueInvalid_ThrowsBadRequest() {
        RatingRequest request = buildRequest(6, null);
        Object[] task = new Object[] { 3, 7, null, "completed" };
        when(ratingRepository.findTaskById(3)).thenReturn(task);
        when(ratingRepository.findDependentUserId(3)).thenReturn(42);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ratingService.submitRating(3, 42, request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void submitRating_WhenValid_ReturnsResponseAndPersistsRatingAndRecalculatesAverage() {
        RatingRequest request = buildRequest(5, "Great help!");
        Object[] task = new Object[] { 3, 7, null, "completed" };
        when(ratingRepository.findTaskById(3)).thenReturn(task);
        when(ratingRepository.findDependentUserId(3)).thenReturn(42);
        when(ratingRepository.isValidRating(5)).thenReturn(true);   // <-- add this

        RatingResponse response = ratingService.submitRating(3, 42, request);

        assertNotNull(response);
        assertEquals("Rating submitted successfully.", response.getMessage());
        assertEquals(3, response.getTaskId());
        assertEquals(5, response.getRating());
        assertEquals("Great help!", response.getReviewSnippet());
        verify(ratingRepository, times(1)).recalculateAverageRating(7, 5);
    }

    @Test
    void getAverageRating_WhenAvailable_ReturnsValue() {
        when(ratingRepository.findAverageRating(7)).thenReturn(4.5);
        Double result = ratingService.getAverageRating(7);
        assertEquals(4.5, result);
    }

    @Test
    void getAverageRating_WhenUnavailable_ReturnsNull() {
        when(ratingRepository.findAverageRating(7)).thenReturn(null);
        Double result = ratingService.getAverageRating(7);
        assertNull(result);
    }
}
