package com.app.api.models;

import java.sql.Timestamp;
import com.app.api.models.ParticipantStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a comment on a post in the community forum.
 * Comments can be nested and include timestamps for tracking creation and updates.
 */

@Data
@Builder 
@Entity
@Table(
    name = "event_participants",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unique_event_participant",
            columnNames = {"event_id", "user_id"}
        )
    }
)
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Integer participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_status", nullable = false)
    private ParticipantStatus participantStatus =
            ParticipantStatus.ATTENDING;

    public EventParticipant() {
        // Required by JPA
    }

    public EventParticipant(Integer participantId, Event event, User user, ParticipantStatus participantStatus) {
        this.participantId = participantId;
        this.event = event;
        this.user = user;
        this.participantStatus = participantStatus;
    }
    
}
