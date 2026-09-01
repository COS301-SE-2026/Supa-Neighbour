package com.app.api.unit.controllers;

import com.app.api.controllers.RatingController;
import com.app.api.dtos.RatingRequest;
import com.app.api.dtos.RatingResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @Mock
    private FirebaseAuthService firebaseAuthService;

    @InjectMocks
    private RatingController ratingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String BEARER_TOKEN = "Bearer valid-token";
    private static final String RAW_TOKEN = "valid-token";
    private static final int CALLER_ID = 42;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ratingController)
                .setValidator(new LocalValidatorFactoryBean())
                .build();
        objectMapper = new ObjectMapper();
    }

    private String ratingRequestJson(String rating, String reviewSnippet) throws Exception {
        RatingRequest request = new RatingRequest();
        request.setRating(rating);
        request.setReviewSnippet(reviewSnippet);
        return objectMapper.writeValueAsString(request);
    }

    // ---------- POST /api/tasks/{taskId}/rate ----------

    @Test
    void rateTask_WhenSuccessful_ReturnsOk() throws Exception {

        RatingResponse response = new RatingResponse("Rating submitted successfully.", 3, "Excellent", "Great help!");
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(ratingService.submitRating(eq(3), eq(CALLER_ID), any(RatingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tasks/3/rate")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(ratingRequestJson("Excellent", "Great help!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rating submitted successfully."))
                .andExpect(jsonPath("$.taskId").value(3))
                .andExpect(jsonPath("$.rating").value("Excellent"));

        verify(ratingService, times(1)).submitRating(eq(3), eq(CALLER_ID), any(RatingRequest.class));
    }

    @Test
    void rateTask_WhenUnauthorized_ReturnsUnauthorized() throws Exception {

        FirebaseAuthException authException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenThrow(authException);

        mockMvc.perform(post("/api/tasks/3/rate")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingRequestJson("Excellent", null)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid or expired Firebase token"));

        verify(ratingService, never()).submitRating(anyInt(), anyInt(), any(RatingRequest.class));
    }

    @Test
    void rateTask_WhenRatingIsBlank_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/tasks/3/rate")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingRequestJson("", null)))
                .andExpect(status().isBadRequest());

        verify(ratingService, never()).submitRating(anyInt(), anyInt(), any(RatingRequest.class));
        verify(firebaseAuthService, never()).getUserIdFromToken(anyString());
    }

    @Test
    void rateTask_WhenUserNotFound_ReturnsNotFound() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(ratingService.submitRating(eq(3), eq(CALLER_ID), any(RatingRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        mockMvc.perform(post("/api/tasks/3/rate")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingRequestJson("Excellent", null)))
                .andExpect(status().isNotFound());
    }

    @Test
    void rateTask_WhenTaskNotFound_ReturnsNotFound() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(ratingService.submitRating(eq(3), eq(CALLER_ID), any(RatingRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        mockMvc.perform(post("/api/tasks/3/rate")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingRequestJson("Excellent", null)))
                .andExpect(status().isNotFound());
    }

    @Test
    void rateTask_WhenAdminSubmits_ReturnsForbidden() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(ratingService.submitRating(eq(3), eq(CALLER_ID), any(RatingRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins are not permitted to submit ratings"));

        mockMvc.perform(post("/api/tasks/3/rate")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingRequestJson("Excellent", null)))
                .andExpect(status().isForbidden());
    }

    @Test
    void rateTask_WhenCallerNotAuthorised_ReturnsForbidden() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(ratingService.submitRating(eq(3), eq(CALLER_ID), any(RatingRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorised to rate task"));

        mockMvc.perform(post("/api/tasks/3/rate")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingRequestJson("Excellent", null)))
                .andExpect(status().isForbidden());
    }

    @Test
    void rateTask_WhenTaskNotCompleted_ReturnsUnprocessableEntity() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(ratingService.submitRating(eq(3), eq(CALLER_ID), any(RatingRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Task must be completed before it can be rated"));

        mockMvc.perform(post("/api/tasks/3/rate")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingRequestJson("Excellent", null)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void rateTask_WhenAlreadyRated_ReturnsConflict() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(ratingService.submitRating(eq(3), eq(CALLER_ID), any(RatingRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT,
                        "You have already submitted a rating for this task"));

        mockMvc.perform(post("/api/tasks/3/rate")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingRequestJson("Excellent", null)))
                .andExpect(status().isConflict());
    }

    @Test
    void rateTask_WhenRatingValueInvalid_ReturnsBadRequest() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(ratingService.submitRating(eq(3), eq(CALLER_ID), any(RatingRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "rating must be one of: Outstanding, Excellent, Very Good, Average"));

        mockMvc.perform(post("/api/tasks/3/rate")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingRequestJson("NotAValidRating", null)))
                .andExpect(status().isBadRequest());
    }
}
