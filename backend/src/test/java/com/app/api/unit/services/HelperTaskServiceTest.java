package com.app.api.unit.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
 
import java.util.List;
 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
 
import com.app.api.dtos.HelperTaskDTO;
import com.app.api.services.HelperTasksService;
import com.app.api.dtos.HelperTaskResponse;
import com.app.api.repositories.HelperTasksRepository;
 
@ExtendWith(MockitoExtension.class)
class HelperTasksServiceTest {
    
        @Mock
    private HelperTasksRepository helperTasksRepository;
 
    @InjectMocks
    private HelperTasksService helperTasksService;
 
    private static final int USER_ID = 7;
    private static final int HELPER_ID = 42;
    private static final int LIMIT = 10;
    private static final int OFFSET = 0;
 
        /** 9-column row shape, matching HelperTaskDTO. */
        private Object[] acceptedRow(int taskId, String taskType, String status, Object startDate, Object endDate,
                      Integer xpWorth, String completionNote,
                      String requesterName, Integer requesterUserId) {
        return new Object[] {
            taskId, taskType, status, startDate, endDate, xpWorth,
            completionNote, requesterName, requesterUserId
        };
    }

    @Test
    void getAcceptedTasks_Throws401_whenUserIsNotHelper() throws Exception {
        when(helperTasksRepository.findHelperByUserId(USER_ID))
            .thenReturn(null);

        assertThatThrownBy(()-> helperTasksService.getAcceptedTasks(USER_ID, null, LIMIT, OFFSET))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex->assertThat(((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN));

        verifyNoMoreInteractions(helperTasksRepository);
    }

    @Test
    void getAcceptedTasks_returnMap_whenUserExists() throws Exception {
        when(helperTasksRepository.findHelperByUserId(USER_ID))
            .thenReturn(HELPER_ID);

        Object[] row1 = acceptedRow(1, "Lawn moving", "assigned", "2026-01-10", null, 20, null, "Sarah Johnson", 101);
        Object[] row2 = acceptedRow(1, "Grocery", "completed", "2026-01-05", "2026-01-06", 15, "Good and fast", "Sarah Johnson", 102);

        when(helperTasksRepository.findAcceptedTasks(HELPER_ID, LIMIT, OFFSET))
            .thenReturn(List.of(row1,row2));
        when(helperTasksRepository.countAcceptedTasks(HELPER_ID))
            .thenReturn(2);

        HelperTaskResponse response = helperTasksService.getAcceptedTasks(USER_ID, null, LIMIT, OFFSET);

        assertThat(response).isNotNull();
        assertThat(response.getHelperId()).isEqualTo(HELPER_ID);
        assertThat(response.getTotal()).isEqualTo(2);
        assertThat(response.getTasks()).hasSize(2);

        HelperTaskDTO dto1 = response.getTasks().get(0);
        assertThat(dto1.getTaskId()).isEqualTo(1);
        assertThat(dto1.getTaskType()).isEqualTo("Lawn moving");
        assertThat(dto1.getStatus()).isEqualTo("assigned");
        assertThat(dto1.getStartDate()).isEqualTo("2026-01-10");
        assertThat(dto1.getEndDate()).isNull();
        assertThat(dto1.getXpAwarded()).isEqualTo(20);
        assertThat(dto1.getCompletionNote()).isNull();
        assertThat(dto1.getRequesterName()).isEqualTo("Sarah Johnson");
        assertThat(dto1.getRequesterUserId()).isEqualTo(101);

        HelperTaskDTO dto2 = response.getTasks().get(1);
        assertThat(dto2.getTaskId()).isEqualTo(1);
        assertThat(dto2.getTaskType()).isEqualTo("Grocery");
        assertThat(dto2.getEndDate()).isEqualTo("2026-01-06");
        assertThat(dto2.getXpAwarded()).isEqualTo(15);
        assertThat(dto2.getCompletionNote()).isEqualTo("Good and fast");
        assertThat(dto2.getRequesterUserId()).isEqualTo(102);
    }

    @Test
    void getAcceptedTasks_retuenEmptList_whenNoRows() throws Exception {
        when(helperTasksRepository.findHelperByUserId(USER_ID))
            .thenReturn(HELPER_ID);
        when(helperTasksRepository.findAcceptedTasks(HELPER_ID, LIMIT, OFFSET))
            .thenReturn(List.<Object[]>of());
        when(helperTasksRepository.countAcceptedTasks(HELPER_ID))
            .thenReturn(0);

        HelperTaskResponse response = helperTasksService.getAcceptedTasks(USER_ID, null, LIMIT, OFFSET);

        assertThat(response.getTasks()).isEmpty();
        assertThat(response.getTotal()).isZero();
    }

    @Test
    void getAcceptedTasks_passesLimitThrough_Repository() throws Exception {
        when(helperTasksRepository.findHelperByUserId(USER_ID))
            .thenReturn(HELPER_ID);
        when(helperTasksRepository.findAcceptedTasks(HELPER_ID, 5, 20))
        .thenReturn(List.<Object[]>of());
        when(helperTasksRepository.countAcceptedTasks(HELPER_ID))
            .thenReturn(0);

        helperTasksService.getAcceptedTasks(USER_ID, null, 5, 20);

        verify(helperTasksRepository).findAcceptedTasks(HELPER_ID, 5, 20);
    }

    @Test
    void getCompletedTasks_throws401_whenUserNotHelper() throws Exception {
        when(helperTasksRepository.findHelperByUserId(USER_ID))
            .thenReturn(null);

        assertThatThrownBy(() -> helperTasksService.getCompletedTasks(USER_ID, LIMIT, OFFSET))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
            .isEqualTo(HttpStatus.FORBIDDEN));

        verify(helperTasksRepository,never()).findCompletedTasks(anyInt(), anyInt(), anyInt());
    }

        @Test
        void getCompletedTasks_returnsMappedTasks_whenRowHasAllTenColumns() {
        // Uses the same 9-column row shape as HelperTaskDTO.
        when(helperTasksRepository.findHelperByUserId(USER_ID)).thenReturn(HELPER_ID);
 
        Object[] row = acceptedRow(3, "Fence repair", "completed", "2025-12-01", "2025-12-02",
            25, "Fixed it perfectly", "Cara Lee", 103);
            when(helperTasksRepository.findCompletedTasks(HELPER_ID, LIMIT, OFFSET))
                .thenReturn(List.<Object[]>of(row));
 
        List<HelperTaskDTO> tasks = helperTasksService.getCompletedTasks(USER_ID, LIMIT, OFFSET);
 
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTaskId()).isEqualTo(3);
        assertThat(tasks.get(0).getTaskType()).isEqualTo("Fence repair");
        assertThat(tasks.get(0).getStatus()).isEqualTo("completed");
        assertThat(tasks.get(0).getXpAwarded()).isEqualTo(25);
        assertThat(tasks.get(0).getCompletionNote()).isEqualTo("Fixed it perfectly");
        assertThat(tasks.get(0).getRequesterUserId()).isEqualTo(103);
 
        verify(helperTasksRepository, never()).countAcceptedTasks(anyInt());
    }

    @Test
    void getCompletedTasks_returnEmptyList_noRows() throws Exception {
        when(helperTasksRepository.findHelperByUserId(USER_ID))
            .thenReturn(HELPER_ID);
        when(helperTasksRepository.findCompletedTasks(HELPER_ID, LIMIT, OFFSET))
            .thenReturn(List.<Object[]>of());

        List<HelperTaskDTO> tasks = helperTasksService.getCompletedTasks(USER_ID, LIMIT, OFFSET);

        assertThat(tasks).isEmpty();
    }

    @Test
    void getCompletedTasks_repositoryReturnsAll() throws Exception {
        when(helperTasksRepository.findHelperByUserId(USER_ID))
            .thenReturn(HELPER_ID);
        
        Object[] row = acceptedRow(4, "Fence repair", "completed", "2025-12-01", "2025-12-02", 25, "Fixed", "James Bond", 104);
        when(helperTasksRepository.findCompletedTasks(HELPER_ID, LIMIT, OFFSET))
            .thenReturn(List.<Object[]>of(row));

        List<HelperTaskDTO> tasks = helperTasksService.getCompletedTasks(USER_ID, LIMIT, OFFSET);

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getRequesterUserId()).isEqualTo(104);
    }
}
