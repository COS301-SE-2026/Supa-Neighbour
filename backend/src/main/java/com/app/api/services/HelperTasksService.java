package com.app.api.services;
 
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.api.dtos.HelperTaskDTO;
import com.app.api.dtos.HelperTaskResponse;
import com.app.api.repositories.HelperTasksRepository;

/**
 * Service responsible for retrieving and processing
 * the task history of authenticated helpers.
 */
@Service
public class HelperTasksService {
    private final HelperTasksRepository helperTasksRepository;

    private static final List<String> VALID_STATUSES = List.of("Invited", "Declined", "assigned", "Rejected", "in_progress", "pending_approval", "completed", "cancelled");

    private static final List<String> INVITATION_STATUSES = List.of("Invited", "Declined", "Rejected", "Accepted");

    
    private static final List<String> INVOICE_STATUSES = List.of("assigned", "in_progress", "pending_approval", "completed", "cancelled");

    /**
     * Constructs a {@code HelperTasksService} with the required repository.
     *
     * @param helperTasksRepository repository used to retrieve helper task
     *                              information
     */
    public HelperTasksService(HelperTasksRepository helperTasksRepository){
        this.helperTasksRepository = helperTasksRepository;
    }

    /**
     * Retrieves the task history for a helper.
     *
     * <p>If a status filter is provided, only tasks matching the specified
     * status are returned. Results may be paginated using the supplied
     * {@code limit} and {@code offset}. A user who is not registered as
     * a helper receives a {@code 403 Forbidden} response.</p>
     *
     * @param userId the identifier of the authenticated user
     * @param statusFilter an optional task status used to filter results
     * @param limit the maximum number of task records to return
     * @param offset the number of task records to skip for pagination
     * @return a {@link HelperTaskResponse} containing the helper's task history
     * @throws ResponseStatusException if the supplied status is invalid or
     *         the user is not registered as a helper
     */
    public HelperTaskResponse getAcceptedTasks(int userId, String statusFilter, int limit, int offset){
        Integer helperId = helperTasksRepository.findHelperByUserId(userId);


        if(helperId == null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a helper");
        }

        List<Object[]> acceptedRows = helperTasksRepository.findAcceptedTasks(helperId, limit, offset);

        List<HelperTaskDTO> tasks = new ArrayList<>();
        for(Object[] row : acceptedRows){
            tasks.add(mapRow(row));
        }

        int total = helperTasksRepository.countAcceptedTasks(helperId);

        return new HelperTaskResponse(helperId, total, tasks);       
    }

    private HelperTaskDTO mapRow(Object[] row){
        return new HelperTaskDTO(((Number) row[0]).intValue(), 
        (String) row[1], 
        (String) row[2], 
        row[3] != null ? row[3].toString() : null, 
        row[4] != null ? row[4].toString() : null,
        row[5] != null ? ((Number) row[5]).intValue() : null,
        row[6] != null ? (String) row[6] : null,
        row[7] != null ? (String) row[7] : null,
        row[8] != null ? ((Number) row[8]).intValue() : null);
    }

    /**
     * Retrieves a helper's completed tasks.
     *
     * <p>
     * Resolves the given user to their helper record, then returns the
     * helper's tasks that have been accepted and completed, mapped to
     * summary DTOs. Results are paginated.
     * </p>
     *
     * @param userId the identifier of the user (must resolve to a helper)
     * @param limit  the maximum number of task records to return
     * @param offset the number of task records to skip for pagination
     * @return a list of completed task summaries for the helper
     * @throws ResponseStatusException with status {@code 403 FORBIDDEN} if the
     *         user does not have an associated helper record
     */
    public List<HelperTaskDTO> getCompletedTasks(int userId, int limit, int offset) {
        Integer helperId = helperTasksRepository.findHelperByUserId(userId);

        if(helperId == null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a helper");
        }

        List<Object[]> completedRows = helperTasksRepository.findCompletedTasks(helperId, limit, offset);

        List<HelperTaskDTO> tasks = new ArrayList<>();
        for(Object[] row : completedRows){
            tasks.add(mapRow(row));
        }

        return tasks;
    }
}
