package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.Dependent;
import  com.app.api.models.EventParticipant;

@Repository 
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Integer> {
   /**
     * Finds dependents belonging to the given user id.
     *
     * @param userId the user id to filter by
     * @return matching dependents
     */
    EventParticipant findByUserId_Userid(int userId);
}
