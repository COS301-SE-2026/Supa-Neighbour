package com.app.api.unit.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.app.api.dtos.MatchedHelperDTO;
import com.app.api.events.HelperMatchedEvent;
import com.app.api.models.Address;
import com.app.api.models.Dependent;
import com.app.api.models.Helper;
import com.app.api.models.HelperSkill;
import com.app.api.models.Location;
import com.app.api.models.TaskInvitation;
import com.app.api.models.TaskInvoice;
import com.app.api.models.TaskType;
import com.app.api.models.User;
import com.app.api.repositories.HelperRepository;
import com.app.api.repositories.HelperSkillRepository;
import com.app.api.repositories.TaskInvitationRepository;
import com.app.api.repositories.TaskInvoiceRepository;
import com.app.api.services.LocationService;
import com.app.api.services.MatchingService;
import com.app.api.services.NotificationsService;


@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private HelperRepository helperRepo;

    @Mock
    private HelperSkillRepository helperSkillRepo;

    @Mock
    private TaskInvitationRepository taskInvitationRepo;

    @Mock
    private TaskInvoiceRepository taskInvoiceRepo;

    @Mock
    private NotificationsService notificationsService;

    @Mock
    private LocationService locationService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private MatchingService matchingService;

    private static final int TASK_ID = 100;
    private static final int REQUESTER_USER_ID = 1;
    private static final String ZONE = "Sandton";
    private static final int TASK_TYPE_ID = 5;

    @BeforeEach
    void setUp() throws Exception {
        matchingService = new MatchingService(
                helperRepo, helperSkillRepo, taskInvitationRepo,
                taskInvoiceRepo, notificationsService, locationService);

        var field = MatchingService.class.getDeclaredField("eventPublisher");
        field.setAccessible(true);
        field.set(matchingService, eventPublisher);
    }

  
    private User mockUser(int userId, String zoneName) {
        User user = mock(User.class);
        lenient().when(user.getUserid()).thenReturn(userId);
        lenient().when(user.getFirstName()).thenReturn("First");
        lenient().when(user.getLastName()).thenReturn("Last" + userId);

        if (zoneName != null) {
            Location location = mock(Location.class);
            lenient().when(location.getNeighbourhoodName()).thenReturn(zoneName);

            Address address = mock(Address.class);
            lenient().when(address.getNeighbourhoodid()).thenReturn(location);

            lenient().when(user.getAddressid()).thenReturn(address);
        } else {
            // null address causes NullPointerException caught in getZoneFromTask/Helper
            lenient().when(user.getAddressid()).thenReturn(null);
        }
        return user;
    }

    
    private TaskInvoice mockTask(int taskId, int requesterUserId,
            String zoneName, Integer taskTypeId) {
        TaskInvoice task = mock(TaskInvoice.class);

        User requester = mockUser(requesterUserId, zoneName);
        Dependent dependent = mock(Dependent.class);
        lenient().when(dependent.getUserId()).thenReturn(requester);
        lenient().when(task.getDependentid()).thenReturn(dependent);

        if (taskTypeId != null) {
            TaskType taskType = mock(TaskType.class);
            lenient().when(taskType.getTasktypeid()).thenReturn(taskTypeId);
            lenient().when(task.getTasktypeid()).thenReturn(taskType);
        } else {
            lenient().when(task.getTasktypeid()).thenReturn(null);
        }
        return task;
    }

   
    private Helper mockHelper(int helperId, int userId, String zoneName, int xp) {
        Helper helper = mock(Helper.class);
        User user = mockUser(userId, zoneName);
        lenient().when(helper.getUserid()).thenReturn(user);
        lenient().when(helper.getHelperid()).thenReturn(helperId);
        lenient().when(helper.getHelperXp()).thenReturn(xp);
        return helper;
    }

    
    private HelperSkill mockSkill(int taskTypeId) {
        HelperSkill skill = mock(HelperSkill.class);
        TaskType taskType = mock(TaskType.class);
        lenient().when(taskType.getTasktypeid()).thenReturn(taskTypeId);
        lenient().when(skill.getTaskTypeId()).thenReturn(taskType);
        return skill;
    }


    @Test
    void returnsNull_whenTaskNotFound() {
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.empty());

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).isNull();
        verifyNoInteractions(helperRepo, helperSkillRepo, taskInvitationRepo, eventPublisher);
    }

    @Test
    void returnsEmptyList_whenRequesterAddressIsNull() {
        TaskInvoice task = mockTask(TASK_ID, REQUESTER_USER_ID, null, TASK_TYPE_ID);
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.of(task));

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).isEmpty();
        verifyNoInteractions(helperRepo, helperSkillRepo, taskInvitationRepo, eventPublisher);
    }


    @Test
    void excludesHelper_whenHelperIsSameUserAsRequester() {
        TaskInvoice task = mockTask(TASK_ID, REQUESTER_USER_ID, ZONE, TASK_TYPE_ID);
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.of(task));

        Helper self = mockHelper(1, REQUESTER_USER_ID, ZONE, 50);
        when(helperRepo.findByAvailable(true)).thenReturn(List.of(self));

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).isEmpty();
        verifyNoInteractions(helperSkillRepo, taskInvitationRepo, eventPublisher);
    }

    @Test
    void excludesHelper_whenHelperAddressIsNull() {
        TaskInvoice task = mockTask(TASK_ID, REQUESTER_USER_ID, ZONE, TASK_TYPE_ID);
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.of(task));

        Helper helper = mockHelper(2, 20, null, 50);
        when(helperRepo.findByAvailable(true)).thenReturn(List.of(helper));

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).isEmpty();
        verifyNoInteractions(taskInvitationRepo, eventPublisher);
    }

    @Test
    void excludesHelper_whenZoneDoesNotMatch() {
        TaskInvoice task = mockTask(TASK_ID, REQUESTER_USER_ID, ZONE, TASK_TYPE_ID);
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.of(task));

        Helper helper = mockHelper(2, 20, "Randburg", 50);
        when(helperRepo.findByAvailable(true)).thenReturn(List.of(helper));

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).isEmpty();
        verifyNoInteractions(taskInvitationRepo, eventPublisher);
    }

    @Test
    void excludesHelper_whenTaskTypeIsNull() {
        // taskTypeId null on the task means skillMatched stays false — helper excluded.
        TaskInvoice task = mockTask(TASK_ID, REQUESTER_USER_ID, ZONE, null);
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.of(task));

        Helper helper = mockHelper(2, 20, ZONE, 50);
        when(helperRepo.findByAvailable(true)).thenReturn(List.of(helper));

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).isEmpty();
        verifyNoInteractions(helperSkillRepo, taskInvitationRepo, eventPublisher);
    }

    @Test
    void excludesHelper_whenNoMatchingSkill() {
        TaskInvoice task = mockTask(TASK_ID, REQUESTER_USER_ID, ZONE, TASK_TYPE_ID);
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.of(task));

        Helper helper = mockHelper(2, 20, ZONE, 50);
        when(helperRepo.findByAvailable(true)).thenReturn(List.of(helper));
        // skill has a different task type — no match
        HelperSkill wrongSkill = mockSkill(TASK_TYPE_ID + 1);
        when(helperSkillRepo.findHelperId(2)).thenReturn(List.of(wrongSkill));

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).isEmpty();
        verify(taskInvitationRepo, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    
    @Test
    void matchesHelper_createsInvitation_andPublishesEvent_whenNewMatch() {
        TaskInvoice task = mockTask(TASK_ID, REQUESTER_USER_ID, ZONE, TASK_TYPE_ID);
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.of(task));

        Helper helper = mockHelper(2, 20, ZONE, 75);
        when(helperRepo.findByAvailable(true)).thenReturn(List.of(helper));
        HelperSkill skill = mockSkill(TASK_TYPE_ID);
        when(helperSkillRepo.findHelperId(2)).thenReturn(List.of(skill));
        when(taskInvitationRepo
                .findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, 2))
                .thenReturn(Optional.empty());

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).hasSize(1);
        MatchedHelperDTO dto = result.get(0);
        assertThat(dto.isSkillMatched()).isTrue();
        assertThat(dto.getHelperXp()).isEqualTo(75);
        assertThat(dto.getHelperId()).isEqualTo(2);

        verify(taskInvitationRepo, times(1)).save(any(TaskInvitation.class));
        verify(eventPublisher, times(1)).publishEvent(any(HelperMatchedEvent.class));
    }

    @Test
    void matchesHelper_doesNotCreateDuplicateInvitation_whenAlreadyExists() {
        TaskInvoice task = mockTask(TASK_ID, REQUESTER_USER_ID, ZONE, TASK_TYPE_ID);
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.of(task));

        Helper helper = mockHelper(2, 20, ZONE, 75);
        when(helperRepo.findByAvailable(true)).thenReturn(List.of(helper));
        HelperSkill skill = mockSkill(TASK_TYPE_ID);
        when(helperSkillRepo.findHelperId(2)).thenReturn(List.of(skill));

        TaskInvitation existing = mock(TaskInvitation.class);
        when(taskInvitationRepo
                .findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, 2))
                .thenReturn(Optional.of(existing));

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).hasSize(1);
        verify(taskInvitationRepo, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }


    @Test
    void sortsByXpDescending_whenBothHelpersSkillMatched() {
        TaskInvoice task = mockTask(TASK_ID, REQUESTER_USER_ID, ZONE, TASK_TYPE_ID);
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.of(task));

        Helper lowXp = mockHelper(2, 20, ZONE, 30);
        Helper highXp = mockHelper(3, 30, ZONE, 90);
        when(helperRepo.findByAvailable(true)).thenReturn(List.of(lowXp, highXp));

        HelperSkill skillLow = mockSkill(TASK_TYPE_ID);
        HelperSkill skillHigh = mockSkill(TASK_TYPE_ID);
        when(helperSkillRepo.findHelperId(2)).thenReturn(List.of(skillLow));
        when(helperSkillRepo.findHelperId(3)).thenReturn(List.of(skillHigh));

        when(taskInvitationRepo
                .findByTaskId_TaskidAndHelperId_Helperid(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getHelperXp()).isEqualTo(90);
        assertThat(result.get(1).getHelperXp()).isEqualTo(30);
    }

    @Test
    void returnsEmptyList_whenNoHelpersAvailable() {
        TaskInvoice task = mockTask(TASK_ID, REQUESTER_USER_ID, ZONE, TASK_TYPE_ID);
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(helperRepo.findByAvailable(true)).thenReturn(List.of());

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).isEmpty();
        verifyNoInteractions(helperSkillRepo, taskInvitationRepo, eventPublisher);
    }

    @Test
    void multipleHelpers_onlyMatchingZoneAndSkillAreReturned() {
        TaskInvoice task = mockTask(TASK_ID, REQUESTER_USER_ID, ZONE, TASK_TYPE_ID);
        when(taskInvoiceRepo.findById(TASK_ID)).thenReturn(Optional.of(task));

        Helper wrongZone = mockHelper(2, 20, "Randburg", 80);
        Helper rightZone = mockHelper(3, 30, ZONE, 60);
        Helper noSkill = mockHelper(4, 40, ZONE, 70);

        when(helperRepo.findByAvailable(true))
                .thenReturn(List.of(wrongZone, rightZone, noSkill));

        HelperSkill matchingSkill = mockSkill(TASK_TYPE_ID);
        HelperSkill wrongSkill = mockSkill(TASK_TYPE_ID + 99);
        when(helperSkillRepo.findHelperId(3)).thenReturn(List.of(matchingSkill));
        when(helperSkillRepo.findHelperId(4)).thenReturn(List.of(wrongSkill));

        when(taskInvitationRepo
                .findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, 3))
                .thenReturn(Optional.empty());

        List<MatchedHelperDTO> result = matchingService.matchHelpersForTask(TASK_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getHelperId()).isEqualTo(3);
    }
}
