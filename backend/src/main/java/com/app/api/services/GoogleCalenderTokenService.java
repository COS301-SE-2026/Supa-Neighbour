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

    public GoogleCalenderTokenService(GoogleCalenderTokenRepository tokenRepo){
        this.tokenRepo = tokenRepo;
    }

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
