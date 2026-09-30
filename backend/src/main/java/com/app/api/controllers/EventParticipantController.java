package com.app.api.controllers;

import com.app.api.dtos.EventParticipantsResponseDTO;
import com.app.api.services.EventParticipantService;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for event participant operations.
 */
@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Operations for managing community events and participants")
public class EventParticipantController {

    private final EventParticipantService eventParticipantService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs the controller with its required dependencies.
     *
     * @param eventParticipantService service for event participant operations
     * @param firebaseAuthService service for verifying Firebase tokens
     */
    public EventParticipantController(EventParticipantService eventParticipantService,
            FirebaseAuthService firebaseAuthService) {
        this.eventParticipantService = eventParticipantService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Returns all participants RSVPed to a specific event.
     *
     * @param eventId the id of the event
     * @param authHeader Firebase Bearer token
     * @return 200 with participants list, 
     *         401 on bad token, 
     *         404 if event not found
     */
    @GetMapping("/{eventId}/participants")
    @Operation(
        summary = "Get event participants",
        description = "Lists all users RSVPed to a specific event.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Participants retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token",
                     content = @Content),
        @ApiResponse(responseCode = "404", description = "Event not found", content = @Content)
    })
    public ResponseEntity<?> getEventParticipants(
            @Parameter(description = "ID of the event to list participants for", example = "7")
            @PathVariable int eventId,
            @Parameter(description = "Firebase Bearer token", required = true)
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);
            EventParticipantsResponseDTO response =
                    eventParticipantService.getParticipantsByEventId(eventId);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Firebase Token");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}
