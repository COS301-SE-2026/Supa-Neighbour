package com.app.api.dtos;

import com.app.api.models.ParticipantStatus;

/**
 * Represents a single participant entry in the event participants response.
 */
public class EventParticipantDTO {

    private int userId;
    private String userName;
    private ParticipantStatus participantStatus;

    /**
     * Constructs an EventParticipantDTO.
     *
     * @param userId the participant's user ID
     * @param userName the participant's username
     * @param participantStatus the participant's current RSVP status
     */
    public EventParticipantDTO(int userId, String userName, ParticipantStatus participantStatus) {
        this.userId = userId;
        this.userName = userName;
        this.participantStatus = participantStatus;
    }


    /** @return the user ID */
    public int getUserId() { 
        return userId; 
    }


    /** @return the username */
    public String getUserName() { 
        return userName; 
    }


    /** @return the participant status */
    public ParticipantStatus getParticipantStatus() {
         return participantStatus; 
    }
}
