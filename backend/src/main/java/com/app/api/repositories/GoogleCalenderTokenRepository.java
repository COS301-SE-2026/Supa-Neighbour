package com.app.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.GoogleCalendarToken;

@Repository
public interface GoogleCalenderTokenRepository extends JpaRepository <GoogleCalendarToken, Integer> {
    /**
     * Finds the Google Calendar token associated with the specified user.
     *
     * @param userid the ID of the user whose Google Calendar token should be retrieved
     * @return an {@link Optional} containing the user's Google Calendar token,
     *         or an empty Optional if no token is found
     */
    Optional<GoogleCalendarToken> findByUserId(int userid);
}
