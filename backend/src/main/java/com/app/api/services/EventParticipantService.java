package com.app.api.services;

import com.app.api.dtos.EventParticipantDTO;
import com.app.api.dtos.EventParticipantsResponseDTO;
import com.app.api.models.EventParticipant;
import com.app.api.repositories.EventParticipantRepository;
import com.app.api.repositories.EventRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service for event participant operations.
 */
@Service
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;

    /**
     * Constructs the service with its required repositories.
     *
     * @param eventParticipantRepository repository for event participants
     * @param eventRepository repository for events
     */
    public EventParticipantService(EventParticipantRepository eventParticipantRepository,
            EventRepository eventRepository) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Returns all participants for the given event.
     *
     * @param eventId the id of the event
     * @return response DTO with participant list and total count
     * @throws ResponseStatusException 404 if the event does not exist
     */
    public EventParticipantsResponseDTO getParticipantsByEventId(int eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event not found"));

        List<EventParticipant> rows = eventParticipantRepository.findByEventId(eventId);

        List<EventParticipantDTO> dtos = rows.stream()
                .map(p -> new EventParticipantDTO(
                        p.getUser().getUserid(),
                        p.getUser().getUsername(),
                        p.getParticipantStatus()))
                .toList();

        return new EventParticipantsResponseDTO(eventId, dtos, dtos.size());
    }
}
