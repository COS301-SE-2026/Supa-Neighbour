package com.app.api.unit.services;

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
import com.app.api.services.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock private UserProfileRepository userProfileRepository;
    @Mock private UserRepository userRepository;
    @Mock private HelperRepository helperRepository;
    @Mock private HelperSkillRepository helperSkillRepository;
    @Mock private TaskTypeRepository taskTypeRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private Object[] coreData;
    private Object[] helperData;

    @BeforeEach
    void setUp() {
        coreData = new Object[]{1, "John S.", "Hillcrest", 2};
        helperData = new Object[]{5, 500, 4.5};
    }

    @Test
    void getProfile_WhenUserNotFound_ThrowsNotFound() {
        when(userProfileRepository.findUserCore(99)).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> userProfileService.getProfile(99));
    }

    @Test
    void getProfile_WhenUserIsNotHelper_ReturnsProfileWithoutHelperData() {
        when(userProfileRepository.findUserCore(1)).thenReturn(coreData);
        when(userProfileRepository.findHelperData(1)).thenReturn(null);
        when(userProfileRepository.countCreatedTasks(1)).thenReturn(3);
        when(userProfileRepository.findEarnedAchievements(1)).thenReturn(List.of());

        UserProfileResponse res = userProfileService.getProfile(1);

        assertNotNull(res);
        assertEquals(1, res.getUserId());
        assertEquals("John S.", res.getDisplayName());
        assertEquals("Hillcrest", res.getNeighbourhood());
        assertNull(res.getLevel());
        assertNull(res.getCurrentXp());
        assertEquals(3, res.getCreatedTasks());
    }

    @Test
    void getProfile_WhenUserIsHelper_ReturnsFullProfile() {
        when(userProfileRepository.findUserCore(1)).thenReturn(coreData);
        when(userProfileRepository.findHelperData(1)).thenReturn(helperData);
        when(userProfileRepository.findHelperRank(5, 2)).thenReturn(1);
        when(userProfileRepository.findSkills(5)).thenReturn(List.of("Pet Care"));
        when(userProfileRepository.countCompletedTasks(5)).thenReturn(10);
        when(userProfileRepository.countActiveTasks(5)).thenReturn(2);
        when(userProfileRepository.findRecentTasks(5)).thenReturn(List.of());
        when(userProfileRepository.countCreatedTasks(1)).thenReturn(5);
        when(userProfileRepository.findEarnedAchievements(1)).thenReturn(List.of());

        UserProfileResponse res = userProfileService.getProfile(1);

        assertNotNull(res);
        assertEquals("Gold", res.getLevel());
        assertEquals(500, res.getCurrentXp());
        assertEquals(4.5, res.getTrustScore());
        assertEquals(10, res.getCompletedTasks());
        assertEquals(2, res.getActiveTasks());
    }

    @Test
    void getProfile_WhenHelperRankIs2_ReturnsSilver() {
        when(userProfileRepository.findUserCore(1)).thenReturn(coreData);
        when(userProfileRepository.findHelperData(1)).thenReturn(helperData);
        when(userProfileRepository.findHelperRank(5, 2)).thenReturn(2);
        when(userProfileRepository.findSkills(5)).thenReturn(List.of());
        when(userProfileRepository.countCompletedTasks(5)).thenReturn(0);
        when(userProfileRepository.countActiveTasks(5)).thenReturn(0);
        when(userProfileRepository.findRecentTasks(5)).thenReturn(List.of());
        when(userProfileRepository.countCreatedTasks(1)).thenReturn(0);
        when(userProfileRepository.findEarnedAchievements(1)).thenReturn(List.of());

        UserProfileResponse res = userProfileService.getProfile(1);
        assertEquals("Silver", res.getLevel());
    }

    @Test
    void getProfile_WhenHelperRankIs3_ReturnsBronze() {
        when(userProfileRepository.findUserCore(1)).thenReturn(coreData);
        when(userProfileRepository.findHelperData(1)).thenReturn(helperData);
        when(userProfileRepository.findHelperRank(5, 2)).thenReturn(3);
        when(userProfileRepository.findSkills(5)).thenReturn(List.of());
        when(userProfileRepository.countCompletedTasks(5)).thenReturn(0);
        when(userProfileRepository.countActiveTasks(5)).thenReturn(0);
        when(userProfileRepository.findRecentTasks(5)).thenReturn(List.of());
        when(userProfileRepository.countCreatedTasks(1)).thenReturn(0);
        when(userProfileRepository.findEarnedAchievements(1)).thenReturn(List.of());

        UserProfileResponse res = userProfileService.getProfile(1);
        assertEquals("Bronze", res.getLevel());
    }

    @Test
    void getProfile_WhenHelperRankIsOther_ReturnsNullLevel() {
        when(userProfileRepository.findUserCore(1)).thenReturn(coreData);
        when(userProfileRepository.findHelperData(1)).thenReturn(helperData);
        when(userProfileRepository.findHelperRank(5, 2)).thenReturn(5);
        when(userProfileRepository.findSkills(5)).thenReturn(List.of());
        when(userProfileRepository.countCompletedTasks(5)).thenReturn(0);
        when(userProfileRepository.countActiveTasks(5)).thenReturn(0);
        when(userProfileRepository.findRecentTasks(5)).thenReturn(List.of());
        when(userProfileRepository.countCreatedTasks(1)).thenReturn(0);
        when(userProfileRepository.findEarnedAchievements(1)).thenReturn(List.of());

        UserProfileResponse res = userProfileService.getProfile(1);
        assertNull(res.getLevel());
    }

    @Test
    void getProfile_WhenHelperHasRecentTasks_MapsThemCorrectly() {
        Object[] taskRow = new Object[]{101, "Pet Care", "2026-07-01", 50};
        when(userProfileRepository.findUserCore(1)).thenReturn(coreData);
        when(userProfileRepository.findHelperData(1)).thenReturn(helperData);
        when(userProfileRepository.findHelperRank(5, 2)).thenReturn(1);
        when(userProfileRepository.findSkills(5)).thenReturn(List.of());
        when(userProfileRepository.countCompletedTasks(5)).thenReturn(1);
        when(userProfileRepository.countActiveTasks(5)).thenReturn(0);
        when(userProfileRepository.findRecentTasks(5)).thenReturn(List.<Object[]>of(taskRow));
        when(userProfileRepository.countCreatedTasks(1)).thenReturn(0);
        when(userProfileRepository.findEarnedAchievements(1)).thenReturn(List.<Object[]>of());

        UserProfileResponse res = userProfileService.getProfile(1);
        assertEquals(1, res.getRecentTasks().size());
        assertEquals(101, res.getRecentTasks().get(0).gettaskId());
    }

    @Test
    void getProfile_WhenRecentTaskHasNullEndDateAndXp_DoesNotThrowAndMapsOne() {
        Object[] taskRow = new Object[]{102, "Home Repair", null, null};
        when(userProfileRepository.findUserCore(1)).thenReturn(coreData);
        when(userProfileRepository.findHelperData(1)).thenReturn(helperData);
        when(userProfileRepository.findHelperRank(5, 2)).thenReturn(1);
        when(userProfileRepository.findSkills(5)).thenReturn(List.of());
        when(userProfileRepository.countCompletedTasks(5)).thenReturn(1);
        when(userProfileRepository.countActiveTasks(5)).thenReturn(0);
        when(userProfileRepository.findRecentTasks(5)).thenReturn(List.<Object[]>of(taskRow));
        when(userProfileRepository.countCreatedTasks(1)).thenReturn(0);
        when(userProfileRepository.findEarnedAchievements(1)).thenReturn(List.<Object[]>of());

        UserProfileResponse res = userProfileService.getProfile(1);
        assertEquals(1, res.getRecentTasks().size());
        assertEquals(102, res.getRecentTasks().get(0).gettaskId());
    }

    @Test
    void getProfile_WhenHelperHasAchievements_MapsThemCorrectly() {
        Object[] achRow = new Object[]{1, "First Steps", "Complete your first task", "2026-05-01"};
        when(userProfileRepository.findUserCore(1)).thenReturn(coreData);
        when(userProfileRepository.findHelperData(1)).thenReturn(null);
        when(userProfileRepository.countCreatedTasks(1)).thenReturn(0);
        when(userProfileRepository.findEarnedAchievements(1)).thenReturn(List.<Object[]>of(achRow));

        UserProfileResponse res = userProfileService.getProfile(1);
        assertEquals(1, res.getAchievements().size());
        assertEquals("First Steps", res.getAchievements().get(0).getName());
    }

    @Test
    void getProfile_WhenAchievementHasNullAwardedOn_DoesNotThrowAndMapsOne() {
        Object[] achRow = new Object[]{2, "Neighbourhood Hero", "Complete ten tasks", null};
        when(userProfileRepository.findUserCore(1)).thenReturn(coreData);
        when(userProfileRepository.findHelperData(1)).thenReturn(null);
        when(userProfileRepository.countCreatedTasks(1)).thenReturn(0);
        when(userProfileRepository.findEarnedAchievements(1)).thenReturn(List.<Object[]>of(achRow));

        UserProfileResponse res = userProfileService.getProfile(1);
        assertEquals(1, res.getAchievements().size());
        assertEquals("Neighbourhood Hero", res.getAchievements().get(0).getName());
    }

    @Test
    void updateProfile_WhenRequestIsEmpty_ThrowsUnprocessableEntity() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        assertThrows(ResponseStatusException.class, () -> userProfileService.updateProfile(1, req));
    }

    @Test
    void updateProfile_WhenUserNotFound_ThrowsNotFound() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFirstName("Jane");
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> userProfileService.updateProfile(99, req));
    }

    @Test
    void updateProfile_WhenOnlyFirstNameProvided_UpdatesFirstName() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFirstName("Jane");

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Smith");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UpdateProfileResponse res = userProfileService.updateProfile(1, req);
        assertEquals("Profile updated", res.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_WhenOnlyLastNameProvided_UpdatesLastName() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setLastName("Nkosi");

        User user = new User();
        user.setFirstName("Thabo");
        user.setLastName("Old");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UpdateProfileResponse res = userProfileService.updateProfile(1, req);
        assertNotNull(res);
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_WhenBothFirstAndLastNameProvided_UpdatesBothInOneSave() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFirstName("Jane");
        req.setLastName("Nkosi");

        User user = new User();
        user.setFirstName("Old");
        user.setLastName("Name");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UpdateProfileResponse res = userProfileService.updateProfile(1, req);
        assertEquals("Jane N.", res.getDisplayName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateProfile_WhenSkillsProvidedAndUserIsNotHelper_ThrowsBadRequest() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setSkills(List.of("Pet Care"));

        User user = new User();
        user.setFirstName("John");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(helperRepository.findByUserid_Userid(1)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userProfileService.updateProfile(1, req));
    }

    @Test
    void updateProfile_WhenSkillsContainInvalidEntry_ThrowsBadRequest() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setSkills(List.of("Pet Care", "InvalidSkill"));

        User user = new User();
        user.setFirstName("John");
        Helper helper = new Helper();
        helper.setHelperid(5);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(helperRepository.findByUserid_Userid(1)).thenReturn(Optional.of(helper));

        TaskType tt = new TaskType();
        tt.setDescription("Pet Care");
        when(taskTypeRepository.findByDescriptionIn(req.getSkills())).thenReturn(List.of(tt));

        assertThrows(ResponseStatusException.class, () -> userProfileService.updateProfile(1, req));
    }

    @Test
    void updateProfile_WhenValidSkillsProvided_UpdatesSkills() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setSkills(List.of("Pet Care"));

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Smith");
        Helper helper = new Helper();
        helper.setHelperid(5);

        TaskType tt = new TaskType();
        tt.setDescription("Pet Care");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(helperRepository.findByUserid_Userid(1)).thenReturn(Optional.of(helper));
        when(taskTypeRepository.findByDescriptionIn(req.getSkills())).thenReturn(List.of(tt));
        when(helperSkillRepository.saveAll(anyList())).thenReturn(List.of(new HelperSkill()));

        UpdateProfileResponse res = userProfileService.updateProfile(1, req);

        assertNotNull(res);
        assertEquals(List.of("Pet Care"), res.getSkills());
        verify(helperSkillRepository).deleteHelperId(5);
        verify(helperSkillRepository).saveAll(anyList());
    }

    @Test
    void updateProfile_WhenSkillsListIsEmpty_ClearsSkills() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setSkills(List.of());

        User user = new User();
        user.setFirstName("John");
        Helper helper = new Helper();
        helper.setHelperid(5);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(helperRepository.findByUserid_Userid(1)).thenReturn(Optional.of(helper));
        when(taskTypeRepository.findByDescriptionIn(List.of())).thenReturn(List.of());
        when(helperSkillRepository.saveAll(anyList())).thenReturn(List.of());

        UpdateProfileResponse res = userProfileService.updateProfile(1, req);

        assertNotNull(res);
        assertEquals(List.of(), res.getSkills());
        verify(helperSkillRepository).deleteHelperId(5);
        verify(helperSkillRepository).saveAll(List.of());
    }

    @Test
    void updateProfile_WhenFirstNameLastNameAndSkillsProvided_UpdatesAll() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFirstName("Jane");
        req.setLastName("Nkosi");
        req.setSkills(List.of("Pet Care"));

        User user = new User();
        user.setFirstName("Old");
        user.setLastName("Name");
        Helper helper = new Helper();
        helper.setHelperid(5);
        TaskType tt = new TaskType();
        tt.setDescription("Pet Care");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(helperRepository.findByUserid_Userid(1)).thenReturn(Optional.of(helper));
        when(taskTypeRepository.findByDescriptionIn(req.getSkills())).thenReturn(List.of(tt));
        when(helperSkillRepository.saveAll(anyList())).thenReturn(List.of(new HelperSkill()));

        UpdateProfileResponse res = userProfileService.updateProfile(1, req);

        assertEquals("Jane N.", res.getDisplayName());
        assertEquals(List.of("Pet Care"), res.getSkills());
        verify(userRepository, times(1)).save(user);
        verify(helperSkillRepository).deleteHelperId(5);
        verify(helperSkillRepository).saveAll(anyList());
    }

    @Test
    void updateProfile_WhenLastNameIsBlank_DisplayNameIsFirstNameOnly() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFirstName("Jane");

        User user = new User();
        user.setFirstName("Old");
        user.setLastName("");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UpdateProfileResponse res = userProfileService.updateProfile(1, req);
        assertEquals("Jane", res.getDisplayName());
    }

    @Test
    void updateProfile_WhenLastNameIsNull_DisplayNameIsFirstNameOnly() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFirstName("Jane");

        User user = new User();
        user.setFirstName("Old");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UpdateProfileResponse res = userProfileService.updateProfile(1, req);
        assertEquals("Jane", res.getDisplayName());
    }
}