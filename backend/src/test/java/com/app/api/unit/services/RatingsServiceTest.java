package com.app.api.unit.services;

import com.app.api.models.Ratings;
import com.app.api.repositories.RatingsRepository;
import com.app.api.services.RatingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingsServiceTest {

    @Mock
    private RatingsRepository ratingsRepository;

    @InjectMocks
    private RatingsService ratingsService;

    private Ratings rating;

    @BeforeEach
    void setUp() {
        rating = new Ratings();
        rating.setRatingid(1);
        rating.setRatingReview("Excellent");
        rating.setTotalXpLevel(500);
        rating.setCurrentGroup("Gold");
    }

    @Test
    void getAllRatings_ReturnsAllRatings() {
        List<Ratings> ratings = Arrays.asList(rating);
        when(ratingsRepository.findAll()).thenReturn(ratings);

        List<Ratings> result = ratingsService.getAllRatings();

        assertEquals(1, result.size());
        assertEquals(rating, result.get(0));
        verify(ratingsRepository, times(1)).findAll();
    }

    @Test
    void getAllRatings_WhenEmpty_ReturnsEmptyList() {
        when(ratingsRepository.findAll()).thenReturn(Arrays.asList());

        List<Ratings> result = ratingsService.getAllRatings();

        assertTrue(result.isEmpty());
    }

    @Test
    void getRatingById_WhenFound_ReturnsRating() {
        when(ratingsRepository.findById(1)).thenReturn(Optional.of(rating));

        Ratings result = ratingsService.getRatingById(1);

        assertNotNull(result);
        assertEquals(1, result.getRatingid());
    }

    @Test
    void getRatingById_WhenNotFound_ReturnsNull() {
        when(ratingsRepository.findById(999)).thenReturn(Optional.empty());

        Ratings result = ratingsService.getRatingById(999);

        assertNull(result);
    }

    @Test
    void saveRating_WhenValid_ReturnsSavedRating() {
        when(ratingsRepository.save(rating)).thenReturn(rating);

        Ratings result = ratingsService.saveRating(rating);

        assertNotNull(result);
        assertEquals(1, result.getRatingid());
        verify(ratingsRepository, times(1)).save(rating);
    }

    @Test
    void saveRating_WhenNull_ReturnsNull() {
        Ratings result = ratingsService.saveRating(null);

        assertNull(result);
        verify(ratingsRepository, never()).save(any());
    }

    @Test
    void updateRating_WhenExists_UpdatesAndReturnsRating() {
        Ratings updated = new Ratings();
        updated.setRatingReview("Outstanding");
        updated.setTotalXpLevel(900);
        updated.setCurrentGroup("Platinum");

        when(ratingsRepository.findById(1)).thenReturn(Optional.of(rating));
        when(ratingsRepository.save(rating)).thenReturn(rating);

        Ratings result = ratingsService.updateRating(1, updated);

        assertNotNull(result);
        assertEquals("Outstanding", result.getRatingReview());
        assertEquals(900, result.getTotalXpLevel());
        assertEquals("Platinum", result.getCurrentGroup());
        verify(ratingsRepository, times(1)).save(rating);
    }

    @Test
    void updateRating_WhenNotExists_ReturnsNull() {
        Ratings updated = new Ratings();
        updated.setRatingReview("Outstanding");

        when(ratingsRepository.findById(999)).thenReturn(Optional.empty());

        Ratings result = ratingsService.updateRating(999, updated);

        assertNull(result);
        verify(ratingsRepository, never()).save(any());
    }

    @Test
    void deleteRating_CallsRepositoryDeleteById() {
        doNothing().when(ratingsRepository).deleteById(1);

        ratingsService.deleteRating(1);

        verify(ratingsRepository, times(1)).deleteById(1);
    }
}
