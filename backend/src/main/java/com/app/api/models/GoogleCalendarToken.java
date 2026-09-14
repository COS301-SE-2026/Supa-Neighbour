package com.app.api.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "google_calendar_token_table")
public class GoogleCalendarToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tokenId;

    @Column(name = "user_id", nullable = false, unique = true)
    private int userId;

    @Column(name ="refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "access_token_expiry")
    private Instant accessTokenExpiry;

    @Column(name = "connected_at")
    private Instant connectedAt;
}
