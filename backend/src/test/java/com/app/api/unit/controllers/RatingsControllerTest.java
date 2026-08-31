package com.app.api.unit.controllers;

import com.app.api.controllers.RatingsController;
import com.app.api.models.Ratings;
import com.app.api.services.RatingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RatingsControllerTest {

    @Mock
    private RatingsService ratingsService;

    @InjectMocks
    private RatingsController ratingsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Ratings rating;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ratingsController).build();
        objectMapper = new ObjectMapper();
        rating = new Ratings();
        rating.setRatingid(1);
        rating.setRatingReview("Excellent");
        rating.setTotalXpLevel(500);
        rating.setCurrentGroup("Gold");
    }

    // ---------- GET /api/ratings ----------

    @Test
    void getAllRatings_ListOfRatings() throws Exception {

        List<Ratings> ratings = Arrays.asList(rating);
        when(ratingsService.getAllRatings()).thenReturn(ratings);

        mockMvc.perform(get("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ratingid").value(1))
                .andExpect(jsonPath("$[0].currentGroup").value("Gold"));

        verify(ratingsService, times(1)).getAllRatings();
    }

    @Test
    void getAllRatings_WhenEmpty_EmptyList() throws Exception {

        when(ratingsService.getAllRatings()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(ratingsService, times(1)).getAllRatings();
    }

    // ---------- GET /api/ratings/{id} ----------

    @Test
    void getRatingsById_ReturnRating() throws Exception {

        when(ratingsService.getRatingById(1)).thenReturn(rating);

        mockMvc.perform(get("/api/ratings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratingid").value(1))
                .andExpect(jsonPath("$.totalXpLevel").value(500));

        verify(ratingsService, times(1)).getRatingById(1);
    }

    @Test
    void getRatingsById_WhenNotExisting_NotFound() throws Exception {

        when(ratingsService.getRatingById(999)).thenReturn(null);

        mockMvc.perform(get("/api/ratings/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(ratingsService, times(1)).getRatingById(999);
    }

    // ---------- POST /api/ratings ----------

    @Test
    void createRatings_CreatedRating() throws Exception {

        when(ratingsService.saveRating(any(Ratings.class))).thenReturn(rating);

        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ratingid").value(1))
                .andExpect(jsonPath("$.currentGroup").value("Gold"));

        verify(ratingsService, times(1)).saveRating(any(Ratings.class));
    }

    // ---------- PUT /api/ratings/{id} ----------

    @Test
    void updateRatings_WhenExists() throws Exception {

        Ratings updatedRating = new Ratings();
        updatedRating.setRatingid(1);
        updatedRating.setRatingReview("Outstanding");
        updatedRating.setTotalXpLevel(900);
        updatedRating.setCurrentGroup("Platinum");

        when(ratingsService.getRatingById(1)).thenReturn(rating);
        when(ratingsService.updateRating(eq(1), any(Ratings.class))).thenReturn(updatedRating);

        mockMvc.perform(put("/api/ratings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRating)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalXpLevel").value(900))
                .andExpect(jsonPath("$.currentGroup").value("Platinum"));

        verify(ratingsService, times(1)).getRatingById(1);
        verify(ratingsService, times(1)).updateRating(eq(1), any(Ratings.class));
    }

    @Test
    void updateRatings_WhenNotExists() throws Exception {

        when(ratingsService.getRatingById(999)).thenReturn(null);

        mockMvc.perform(put("/api/ratings/999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isNotFound());

        verify(ratingsService, times(1)).getRatingById(999);
        verify(ratingsService, never()).updateRating(anyInt(), any(Ratings.class));
    }

    // ---------- DELETE /api/ratings/{id} ----------

    @Test
    void deleteRatings_WhenExisting() throws Exception {

        when(ratingsService.getRatingById(1)).thenReturn(rating);
        doNothing().when(ratingsService).deleteRating(1);

        mockMvc.perform(delete("/api/ratings/1"))
                .andExpect(status().isNoContent());

        verify(ratingsService, times(1)).getRatingById(1);
        verify(ratingsService, times(1)).deleteRating(1);
    }

    @Test
    void deleteRatings_WhenNotExisting() throws Exception {

        when(ratingsService.getRatingById(999)).thenReturn(null);

        mockMvc.perform(delete("/api/ratings/999"))
                .andExpect(status().isNotFound());

        verify(ratingsService, times(1)).getRatingById(999);
        verify(ratingsService, never()).deleteRating(anyInt());
    }
}
