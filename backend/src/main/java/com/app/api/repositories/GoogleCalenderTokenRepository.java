package com.app.api.repositories;

import com.app.api.models.GoogleCalendarToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleCalenderTokenRepository extends JpaRepository <GoogleCalendarToken, Integer> {
    Optional<GoogleCalendarToken> findByUserId(int userid);
}
