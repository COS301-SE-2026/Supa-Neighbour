package com.app.api.services;
 
import com.app.api.dtos.HelperTaskDTO;
import com.app.api.dtos.HelperTaskResponse;
import com.app.api.repositories.HelperTasksRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
 
import java.util.ArrayList;
import java.util.List;

@Service
public class HelperTasksService {
    private final HelperTasksRepository helperTasksRepository;

    private static final List<String> VALID_STATUSES = List.of("Invited", "Declined", "assigned", "in_progress", "pending_approval", "completed", "cancelled");

    private static final List<String> INVITATION_STATUSES = List.of("Invited", "Declined");

    
    private static final List<String> INVOICE_STATUSES = List.of("assigned", "in_progress", "pending_approval", "completed", "cancelled");

    public HelperTasksService(HelperTasksRepository helperTasksRepository){
        this.helperTasksRepository = helperTasksRepository;
    }

    public HelperTaskResponse getTasks(int userId, String statusFilter, int limit, int offset){
        if (statusFilter != null && statusFilter.isBlank()) {
            statusFilter = null;
        }

        if(statusFilter != null && !VALID_STATUSES.contains(statusFilter)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be one of: Invited, Declined, assigned, in_progress, " + "pending_approval, completed, cancelled");
        }

        Integer helperId = helperTasksRepository.findHelperByUserId(userId);


        if(helperId == null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a helper");
        }

        List<Object[]> invitedRows = new ArrayList<>();
        List<Object[]> assignedRows = new ArrayList<>();

        boolean fetchInvited  = statusFilter == null || (statusFilter != null && INVITATION_STATUSES.contains(statusFilter));
        boolean fetchAssigned = statusFilter == null || (statusFilter != null && INVOICE_STATUSES.contains(statusFilter));

        if(fetchInvited){
            invitedRows  = helperTasksRepository.findInvitedTasks(helperId, statusFilter != null && INVITATION_STATUSES.contains(statusFilter) ? statusFilter : null, limit, offset);
        }

        if(fetchAssigned){
            assignedRows = helperTasksRepository.findAssignedTasks(helperId, statusFilter != null && INVOICE_STATUSES.contains(statusFilter) ? statusFilter : null, limit, offset);
        }

        List<HelperTaskDTO> tasks = new ArrayList<>();
        for(Object[] row: invitedRows){
            tasks.add(mapRow(row));
        }

        for(Object[] row : assignedRows){
            tasks.add(mapRow(row));
        }

        int total = helperTasksRepository.countAllTasks(helperId);

        return new HelperTaskResponse(helperId, total, tasks);
    }

    private HelperTaskDTO mapRow(Object[] row){
        return new HelperTaskDTO(((Number) row[0]).intValue(), (String) row[1], (String) row[2], row[3] != null ? row[3].toString() : null, row[4] != null ? row[4].toString() : null, (String) row[5], row[6] != null ? ((Number) row[6]).intValue() : null);
    }
}
