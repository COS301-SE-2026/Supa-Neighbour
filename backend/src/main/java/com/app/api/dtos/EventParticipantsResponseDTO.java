package com.app.api.dtos;

import java.util.List;


public class EventParticipantsResponseDTO {

    private int eventId;
    private List<EventParticipantDTO> participants;
    private int totalCount;

    /**
     * Constructs an EventParticipantsResponseDTO.
     *
     * @param eventId the event ID
     * @param participants the list of participant DTOs
     * @param totalCount total number of participants
     */
    public EventParticipantsResponseDTO(int eventId,
            List<EventParticipantDTO> participants, int totalCount) {
        this.eventId = eventId;
        this.participants = participants;
        this.totalCount = totalCount;
    }


    /** @return the event ID */
    public int getEventId() {
        return eventId;
    }


    /** @return the participants list */
    public List<EventParticipantDTO> getParticipants() {
        return participants;
    }

    
    /** @return the total participant count */
    public int getTotalCount() {
        return totalCount;
    }
}
