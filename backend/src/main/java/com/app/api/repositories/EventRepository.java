package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.Event;
import com.app.api.models.EventParticipant;

public interface EventRepository extends JpaRepository<Event,Integer> {
    /**
     * Finds dependents belonging to the given user id.
     *
     * @param userId the user id to filter by
     * @return matching dependents
     */
    Event findByUserId_Userid(int userId);
}
