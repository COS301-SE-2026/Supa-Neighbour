package com.app.api.unit.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
 
import java.util.List;
 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
 
import com.app.api.dtos.HelperProfileResponse;
import com.app.api.dtos.ReviewDTO;
import com.app.api.repositories.HelperprofileRepository;
import com.app.api.services.HelperProfileService;
 

@ExtendWith(MockitoExtension.class)
class HelperProfileServiceTest {
 
    @Mock
    private HelperprofileRepository helperProfileRepository;
 
    @InjectMocks
    private HelperProfileService helperProfileService;
 
    private static final int HELPER_ID = 42;
    private static final int USER_ID = 7;
    private static final int NEIGHBOURHOOD_ID = 3;
 
    private Object[] coreRow(String displayName, double trustScore, int neighbourhoodId) {
        // index 0 = h.helper_id (queried but not read by the service, since
        // getProfile already has the helperId parameter it needs)
        return new Object[] { HELPER_ID, displayName, trustScore, neighbourhoodId };
    }
 
    // ---------- getProfile(int) ----------
 
    @Test
    void getProfile_throwsNotFound_whenCoreIsNull() {
        when(helperProfileRepository.findHelperCore(HELPER_ID)).thenReturn(null);
 
        assertThatThrownBy(() -> helperProfileService.getProfile(HELPER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
 
        verifyNoMoreInteractions(helperProfileRepository);
    }
 
    @Test
    void getProfile_buildsResponse_withGoldLevel_whenRankIsOne() {
        when(helperProfileRepository.findHelperCore(HELPER_ID))
                .thenReturn(coreRow("Alice", 4.8, NEIGHBOURHOOD_ID));
        when(helperProfileRepository.findHelperRank(HELPER_ID, NEIGHBOURHOOD_ID)).thenReturn(1);
        when(helperProfileRepository.CompletedTasks(HELPER_ID)).thenReturn(12);
        when(helperProfileRepository.countNeighboursHelped(HELPER_ID)).thenReturn(9);
        when(helperProfileRepository.findSkills(HELPER_ID)).thenReturn(List.of("Gardening", "Moving"));
        when(helperProfileRepository.findReviews(HELPER_ID)).thenReturn(List.of());
 
        HelperProfileResponse response = helperProfileService.getProfile(HELPER_ID);
 
        assertThat(response).isNotNull();
        assertThat(response.getHelperId()).isEqualTo(HELPER_ID);
        assertThat(response.getDisplayName()).isEqualTo("Alice");
        assertThat(response.getLevel()).isEqualTo("Gold");
        assertThat(response.getTrustScore()).isEqualTo(4.8);
        assertThat(response.getCompletedTasks()).isEqualTo(12);
        assertThat(response.getNeighboursHelped()).isEqualTo(9);
        assertThat(response.getSkills()).containsExactly("Gardening", "Moving");
        assertThat(response.getReviews()).isEmpty();
    }

    @Test
    void getProfile_checkLevel_forSilverAndBronzeAndUnknown() throws Exception {
        //Silvier
        when(helperProfileRepository.findHelperCore(HELPER_ID))
            .thenReturn(coreRow("James",4.0,NEIGHBOURHOOD_ID));
        when(helperProfileRepository.findHelperRank(HELPER_ID, NEIGHBOURHOOD_ID))
            .thenReturn(2);

        when(helperProfileRepository.CompletedTasks(HELPER_ID)).thenReturn(5);
        when(helperProfileRepository.countNeighboursHelped(HELPER_ID))
            .thenReturn(3);

        when(helperProfileRepository.findSkills(HELPER_ID))
            .thenReturn(List.of());
        when(helperProfileRepository.findReviews(HELPER_ID))
            .thenReturn(List.of());

        assertThat(helperProfileService.getProfile(HELPER_ID).getLevel())
            .isEqualTo("Silver");
    }

    @Test
    void getProfile_mapsLevel_toBronze_whenRankIsThree() throws Exception {
                //Silvier
        when(helperProfileRepository.findHelperCore(HELPER_ID))
            .thenReturn(coreRow("Sarah",3.5,NEIGHBOURHOOD_ID));
        when(helperProfileRepository.findHelperRank(HELPER_ID, NEIGHBOURHOOD_ID))
            .thenReturn(3);

        when(helperProfileRepository.CompletedTasks(HELPER_ID)).thenReturn(2);
        when(helperProfileRepository.countNeighboursHelped(HELPER_ID))
            .thenReturn(1);

        when(helperProfileRepository.findSkills(HELPER_ID))
            .thenReturn(List.of());
        when(helperProfileRepository.findReviews(HELPER_ID))
            .thenReturn(List.of());

        assertThat(helperProfileService.getProfile(HELPER_ID).getLevel())
            .isEqualTo("Bronze");
    }

    @Test
    void getProfile_levelNull_rankOutside_topThree() throws Exception {
        when(helperProfileRepository.findHelperCore(HELPER_ID))
            .thenReturn(coreRow("Micheal",2.9,NEIGHBOURHOOD_ID));
        when(helperProfileRepository.findHelperRank(HELPER_ID, NEIGHBOURHOOD_ID))
            .thenReturn(7);

        when(helperProfileRepository.CompletedTasks(HELPER_ID))
            .thenReturn(1);
        when(helperProfileRepository.countNeighboursHelped(HELPER_ID))
            .thenReturn(1);

        when(helperProfileRepository.findSkills(HELPER_ID))
            .thenReturn(List.of());
        when(helperProfileRepository.findReviews(HELPER_ID))
            .thenReturn(List.of());

        assertThat(helperProfileService.getProfile(HELPER_ID).getLevel()).isNull();
    }

    @Test
    void getProfile_mapReviews_nullField() throws Exception {
        when(helperProfileRepository.findHelperCore(HELPER_ID))
            .thenReturn(coreRow("Evea",4.2,NEIGHBOURHOOD_ID));
        when(helperProfileRepository.findHelperRank(HELPER_ID, NEIGHBOURHOOD_ID))
            .thenReturn(5);
        
        when(helperProfileRepository.CompletedTasks(HELPER_ID))
            .thenReturn(4);
        when(helperProfileRepository.countNeighboursHelped(HELPER_ID))
            .thenReturn(2);

        when(helperProfileRepository.findSkills(HELPER_ID))
            .thenReturn(List.of("Plant Care"));

        Object[] reviewWithDate = {"5 stars - so goo", "get help",java.time.LocalDate.of(2026,10,05) };
        Object[] reviewWithoutDate = {"4 stars","very relyable",null };

        when(helperProfileRepository.findReviews(HELPER_ID))
            .thenReturn(List.of(reviewWithDate,reviewWithoutDate));

        List<ReviewDTO> reviews = helperProfileService.getProfile(HELPER_ID).getReviews();

        assertThat(reviews).hasSize(2);
        assertThat(reviews.get(0).getRating()).isEqualTo("5 stars - so goo");
        assertThat(reviews.get(0).getSnippet()).isEqualTo("get help");
        assertThat(reviews.get(0).getDate()).isEqualTo("2026-10-05");

        assertThat(reviews.get(1).getRating()).isEqualTo("4 stars");
        assertThat(reviews.get(1).getSnippet()).isEqualTo("very relyable");
        assertThat(reviews.get(1).getDate()).isNull();
    }

    @Test
    void getProfile_throws404_whenNoHelper() throws Exception {
        when(helperProfileRepository.findHelperIdByUserId(USER_ID))
            .thenReturn(null);
        assertThatThrownBy(() -> helperProfileService.getProfileByUserId(USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
 
        verify(helperProfileRepository, never()).findHelperCore(anyInt());
    }

    @Test
    void getProfileByUser_delegatesWhen_helperExists() throws Exception {
        when(helperProfileRepository.findHelperIdByUserId(USER_ID)).thenReturn(HELPER_ID);
        when(helperProfileRepository.findHelperCore(HELPER_ID))
                .thenReturn(coreRow("Hank", 4.5, NEIGHBOURHOOD_ID));
        when(helperProfileRepository.findHelperRank(HELPER_ID, NEIGHBOURHOOD_ID)).thenReturn(1);
        when(helperProfileRepository.CompletedTasks(HELPER_ID)).thenReturn(8);
        when(helperProfileRepository.countNeighboursHelped(HELPER_ID)).thenReturn(6);
        when(helperProfileRepository.findSkills(HELPER_ID)).thenReturn(List.of("Pet sitting"));
        when(helperProfileRepository.findReviews(HELPER_ID)).thenReturn(List.of());
 
        HelperProfileResponse response = helperProfileService.getProfileByUserId(USER_ID);
 
        assertThat(response.getHelperId()).isEqualTo(HELPER_ID);
        assertThat(response.getDisplayName()).isEqualTo("Hank");
        assertThat(response.getLevel()).isEqualTo("Gold");
    }
}

