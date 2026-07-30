package com.app.api.unit.controllers;

import com.app.api.models.Badges;
import com.app.api.services.BadgesService;
import com.app.api.controllers.BadgesController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.api.models.Ratings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
    value = BadgesController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {com.app.api.security.FirebaseAuthenticationFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class BadgeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BadgesService badgesService;

    private Badges sampleBadge;
    private Ratings sampleRatings;

    @BeforeEach
    void setup(){
        reset(badgesService);
        sampleRatings = new Ratings(3, "Excellent", 500, "Veteran");

        sampleBadge = new Badges();
        sampleBadge.setBadgeid(1);
        sampleBadge.setBadgeName("Helping Hand");
        sampleBadge.setBadgeDescription("Completed 5 tasks");
        sampleBadge.setXpReward(50);
        sampleBadge.setRatingid(sampleRatings);
        sampleBadge.setIsSpecialist(false);
    }

    @Test
    void getAllBadges_returnsOkWithList() throws Exception{
        when(badgesService.getAllBadges()).thenReturn(List.of(sampleBadge));
        mockMvc.perform(get("/api/badges"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].badgeid").value(1))
        .andExpect(jsonPath("$.[0].badgeName").value("Helping Hand"));

        verify(badgesService).getAllBadges();
    }

    @Test
    void getBadgeId_found_returnsOk() throws Exception{
        when(badgesService.getBadgesById(1)).thenReturn(sampleBadge);

        mockMvc.perform(get("/api/badges/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.badgeName").value("Helping Hand"))
        .andExpect(jsonPath("$.badgeid").value(1))
        .andExpect(jsonPath("$.xpReward").value(50))
        .andExpect(jsonPath("$.ratingid.ratingReview").value("Excellent"))
        .andExpect(jsonPath("$.isSpecialist").value(false));
    }

    @Test
    void getBadgesById_notFound_returns404() throws Exception{
        when(badgesService.getBadgesById(99)).thenReturn(null);

        mockMvc.perform(get("/api/badges/99")).andExpect(status().isNotFound());
    }

    @Test
    void createBadge_returnsCreatedWithBody() throws Exception{
        when(badgesService.saveBadges(any(Badges.class))).thenReturn(sampleBadge);

        mockMvc.perform(post("/api/badges")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(sampleBadge)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.badgeid").value(1))
            .andExpect(jsonPath("$.badgeName").value("Helping Hand"));

            verify(badgesService).saveBadges(any(Badges.class));
    }

    @Test
    void updateBadge_existing_returnsOkWithUpdatedBody() throws Exception{
        
        Ratings newRating = new Ratings(3, "Not Good", 500, "Veteran");
        Badges updateInput = new Badges();

        updateInput.setBadgeName("Neighbourhood Hero");
        updateInput.setBadgeDescription("Completed 25 tasks");
        updateInput.setXpReward(200);
        updateInput.setRatingid(newRating);
        updateInput.setIsSpecialist(true);

        Badges updateResult = new Badges();
        updateResult.setBadgeid(1);
        updateResult.setBadgeName("Neighbourhood Hero");
        updateResult.setBadgeDescription("Completed 25 tasks");
        updateResult.setXpReward(200);
        updateResult.setRatingid(newRating);
        updateResult.setIsSpecialist(true);

        when(badgesService.getBadgesById(1)).thenReturn(sampleBadge);
        when(badgesService.updateBadges(eq(1), any(Badges.class))).thenReturn(updateResult);

        mockMvc.perform(put("/api/badges/1")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.badgeName").value("Neighbourhood Hero"))
            .andExpect(jsonPath("$.xpReward").value(200))
            .andExpect(jsonPath("$.isSpecialist").value(true));
    }

    @Test
     void updateBadge_notFound_returns404() throws Exception{
        when(badgesService.getBadgesById(99)).thenReturn(null);

        mockMvc.perform(put("/api/badges/99")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(sampleBadge)))
            .andExpect(status().isNotFound());

        verify(badgesService, never()).updateBadges(anyInt(), any(Badges.class));
    }

    @Test
    void deleteBadge_existing_returnsNoContent() throws Exception{
        when(badgesService.getBadgesById(1)).thenReturn(sampleBadge);

        mockMvc.perform(delete("/api/badges/1"))
        .andExpect(status().isNoContent());

        verify(badgesService).deleteBadges(1);
    }

    @Test
    void deleteBadge_notFound_returns404() throws Exception{
        when(badgesService.getBadgesById(99)).thenReturn(null);

        mockMvc.perform(delete("/api/badges/99"))
        .andExpect(status().isNotFound());

        verify(badgesService, never()).deleteBadges(anyInt());
    }



}
