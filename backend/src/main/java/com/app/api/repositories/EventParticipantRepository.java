package com.app.api.repositories;

import com.app.api.models.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for EventParticipant entities.
 */
@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Integer> {

    /**
     * Returns all participants for a given event, with user eagerly loaded.
     *
     * @param eventId the id of the event
     * @return list of participants
     */
    @Query("SELECT p FROM EventParticipant p JOIN FETCH p.user WHERE p.event.enventId = :eventId")
    List<EventParticipant> findByEventId(@Param("eventId") int eventId);

    /**
     * Returns a participant row for a given event and user combination.
     *
     * @param eventId the event id
     * @param userId the user id
     * @return the participant row if it exists
     */
    @Query("SELECT p FROM EventParticipant p WHERE p.event.enventId = :eventId AND p.user.userid = :userId")
    Optional<EventParticipant> findByEventIdAndUserId(
            @Param("eventId") int eventId, @Param("userId") int userId);
}
