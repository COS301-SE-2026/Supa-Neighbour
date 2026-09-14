package com.app.api.services;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.app.api.models.GoogleCalendarToken;
import com.app.api.repositories.GoogleCalenderTokenRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@Service
public class GoogleCalenderTokenService {
    private final GoogleCalenderTokenRepository tokenRepo;

    @Value("${google.oauth.client-id}")
    private String clientId;

    @Value("${google.oauth.client-secret}")
    private String clientSecret;

    @Value("${google.oauth.redirect-uri}")
    private String redirectUri;

    /**
     * Constructs a new {@code GoogleCalenderTokenService}.
     *
     * @param tokenRepo repository used to retrieve and store Google Calendar
     *                  authentication tokens
     */
    public GoogleCalenderTokenService(GoogleCalenderTokenRepository tokenRepo){
        this.tokenRepo = tokenRepo;
    }

    /**
     * Exchanges a Google OAuth authorization code for access and refresh
     * tokens and stores the resulting credentials for the specified user.
     *
     * <p>If the user already has a stored Google Calendar token, the existing
     * token record is updated. Otherwise, a new token record is created.</p>
     *
     * <p>The access token expiry time is calculated using the number of seconds
     * until the access token expires, as provided by Google's OAuth response.</p>
     *
     * @param userId the ID of the user connecting their Google Calendar account
     * @param authCode the authorization code received from Google
     * @throws IOException if an error occurs while communicating with
     *                     Google's OAuth service
     */
    public void exchangeAndStore(int userId, String authCode) throws IOException {
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                clientId,
                clientSecret,
                authCode,
                redirectUri)
                .execute();

        GoogleCalendarToken token = tokenRepo.findByUserId(userId).orElseGet(GoogleCalendarToken::new);
        token.setUserId(userId);
        token.setRefreshToken(tokenResponse.getRefreshToken());
        token.setAccessToken(tokenResponse.getAccessToken());
        token.setAccessTokenExpiry(Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds()));
        token.setConnectedAt(Instant.now());
        tokenRepo.save(token);
    }
}
