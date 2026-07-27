package com.app.api.services;

import com.app.api.dtos.MatchedHelperDTO;
import com.app.api.models.Helper;
import com.app.api.models.HelperSkill;
import com.app.api.models.TaskInvitation;
import com.app.api.models.TaskInvoice;
import com.app.api.repositories.HelperRepository;
import com.app.api.repositories.HelperSkillRepository;
import com.app.api.repositories.TaskInvitationRepository;
import com.app.api.repositories.TaskInvoiceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service class responsible for matching helpers to tasks based on various criteria such as location and skills.
 */
@Service
public class MatchingService {

    private final HelperRepository helperRepo;
    private final HelperSkillRepository helperSkillRepo;
    private final TaskInvitationRepository taskInvitationRepo;
    private final TaskInvoiceRepository taskInvoiceRepo;

    /**
     * Constructs a MatchingService with the specified repositories.
     *
     * @param helperRepo the repository for managing helpers
     * @param helperSkillRepo the repository for managing helper skills
     * @param taskInvitationRepo the repository for managing task invitations
     * @param taskInvoiceRepo the repository for managing task invoices
     */
    public MatchingService(HelperRepository helperRepo,
            HelperSkillRepository helperSkillRepo,
            TaskInvitationRepository taskInvitationRepo,
            TaskInvoiceRepository taskInvoiceRepo) {
        this.helperRepo = helperRepo;
        this.helperSkillRepo = helperSkillRepo;
        this.taskInvitationRepo = taskInvitationRepo;
        this.taskInvoiceRepo = taskInvoiceRepo;
    }

    /**
     * Matches helpers to a task based on location and skills.
     *
     * @param taskId the ID of the task for which to find matching helpers
     * @return a list of matched helpers with their details
     */
    @Transactional
    public List<MatchedHelperDTO> matchHelpersForTask(int taskId) {

        TaskInvoice task = taskInvoiceRepo.findById(taskId).orElse(null);
        if (task == null) {
            return null;
        }

        String requesterZone = getZoneFromTask(task);
        if (requesterZone == null) {
            return new ArrayList<>();
        }

        Integer taskTypeId = task.getTasktypeid() != null 
            ? task.getTasktypeid().getTasktypeid()
            : null;


        List<Helper> availableHelpers = helperRepo.findByAvailable(true);

        List<MatchedHelperDTO> matched = new ArrayList<>();

        for (Helper helper : availableHelpers) {

            String helperZone = getZoneFromHelper(helper);
            if (helperZone == null) {
                continue;
            }

            if (!helperZone.equalsIgnoreCase(requesterZone)) {
                continue;
            }

            boolean skillMatched = false;
            if (taskTypeId != null) {
                List<HelperSkill> skills = helperSkillRepo.findHelperId(helper.getHelperid());
                for (HelperSkill skill : skills) {
                if (skill.getTaskTypeId() != null
                        && skill.getTaskTypeId().getTasktypeid() == taskTypeId) {
                        skillMatched = true;
                        break;
                    }
                }
            }

            boolean alreadyInvited = taskInvitationRepo
                    .findByTaskId_TaskidAndHelperId_Helperid(taskId, helper.getHelperid())
                    .isPresent();

            if (!alreadyInvited) {
                TaskInvitation invitation = TaskInvitation.builder()
                        .taskId(task)
                        .helperId(helper)
                        .status("Invited")
                        .invitedAt(new Date())
                        .build();
                taskInvitationRepo.save(invitation);
            }

            String helperName = helper.getUserid() != null
                    ? helper.getUserid().getFirstName() + " " + helper.getUserid().getLastName()
                    : "Unknown";

            matched.add(new MatchedHelperDTO(
                    helper.getHelperid(),
                    helperName,
                    helperZone,
                    skillMatched,
                    helper.getHelperXp(),
                    "Invited"
            ));
        }

        matched.sort((a, b) -> {
            if (a.isSkillMatched() != b.isSkillMatched()) {
                return a.isSkillMatched() ? -1 : 1;
            }
            return Integer.compare(b.getHelperXp(), a.getHelperXp());
        });

        return matched;
    }

    /**
     * Retrieves the neighbourhood zone from a given task.
     *
     * @param task the task from which to extract the zone
     * @return the neighbourhood zone, or null if not available
     */
    private String getZoneFromTask(TaskInvoice task) {
    try {
            return task.getDependentid()
                    .getUserId()
                    .getAddressid()
                    .getNeighbourhoodid()
                    .getNeighbourhoodName();

        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Retrieves the neighbourhood zone from a given helper.
     *
     * @param helper the helper from which to extract the zone
     * @return the neighbourhood zone, or null if not available
     */
    private String getZoneFromHelper(Helper helper) {
        try {
            return helper.getUserid()
                    .getAddressid()
                    .getNeighbourhoodid()
                    .getNeighbourhoodName();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
