package com.app.api.services;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.app.api.events.TaskAcceptedEvent;
import com.app.api.models.GoogleCalendarToken;
import com.app.api.models.TaskInvoice;
import com.app.api.repositories.GoogleCalenderTokenRepository;
import com.app.api.repositories.TaskInvoiceRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GoogleCalenderTokenService {
    private static final String TIMEZONE = "Africa/Johannesburg";
    private static final String APP_NAME = "Supa-Neighbour";
    private static final Logger LOG = LoggerFactory.getLogger(GoogleCalenderTokenService.class);

    private final GoogleCalenderTokenRepository tokenRepo;
    private final TaskInvoiceRepository taskInvoiceRepository;

    /**
     * Normalizes a Google OAuth environment value by trimming surrounding quotes.
     *
     * <p>Some deployments load credentials from .env files with single or double
     * quotes, which must be removed before exchanging the authorization code.</p>
     *
     * @param value the raw configuration value from the environment or properties
     * @return the trimmed value without wrapping quotes
     */
    public static String normalizeGoogleConfigValue(String value) {
        if (value == null) {
            return "";
        }

        String normalized = value.trim();
        if (normalized.length() >= 2) {
            char first = normalized.charAt(0);
            char last = normalized.charAt(normalized.length() - 1);
            if ((first == '\'' && last == '\'') || (first == '"' && last == '"')) {
                normalized = normalized.substring(1, normalized.length() - 1).trim();
            }
        }

        return normalized;
    }

    /**
     * Normalizes a Google OAuth client ID so it matches the format expected by
     * the Google token exchange endpoint.
     *
     * <p>Some environments store the web client ID without the required
     * {@code .apps.googleusercontent.com} suffix. When that happens, Google
     * rejects the authorization code exchange with an invalid grant.</p>
     *
     * @param value the raw client ID from env/configuration
     * @return a normalized client ID that is safe to use in token exchange requests
     */
    public static String normalizeGoogleClientId(String value) {
        String normalized = normalizeGoogleConfigValue(value);
        if (normalized.isBlank()) {
            return "";
        }

        if (normalized.contains(".apps.googleusercontent.com")) {
            return normalized;
        }

        if (normalized.matches("^[A-Za-z0-9-]+$")) {
            return normalized + ".apps.googleusercontent.com";
        }

        return normalized;
    }

    /**
     * Resolves the redirect URI used for Google OAuth token exchange.
     *
     * @param value the configured redirect URI, if any
     * @return {@code postmessage} for Google Sign-In requests, or the configured value
     */
    public static List<String> resolveRedirectUriCandidates(String value) {
        String normalized = normalizeGoogleConfigValue(value);
        Set<String> candidates = new LinkedHashSet<>();

        candidates.add("");                 // Android/iOS native serverAuthCode
        candidates.add("postmessage");      // web popup flow
        if (!normalized.isBlank()) {
            candidates.add(normalized);     // explicit configured URI
        }
        return new ArrayList<>(candidates);
    }

    /**
     * Selects the first redirect URI candidate that should be used for the Google
     * OAuth code exchange.
     *
     * <p>Google Sign-In commonly uses {@code postmessage}, but some deployments
     * still register a concrete callback URI. We prefer the Sign-In default and
     * fall back to any configured value if present.</p>
     *
     * @param value the configured redirect URI value from environment or config
     * @return the preferred redirect URI to use for the code exchange
     */
    public static String resolveRedirectUri(String value) {
        List<String> candidates = resolveRedirectUriCandidates(value);
        return candidates.isEmpty() ? "postmessage" : candidates.getFirst();
    }

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
    public GoogleCalenderTokenService(GoogleCalenderTokenRepository tokenRepo, TaskInvoiceRepository taskInvoiceRepository){
        this.tokenRepo = tokenRepo;
        this.taskInvoiceRepository = taskInvoiceRepository;
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
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user id");
        }
        if (authCode == null || authCode.isBlank()) {
            throw new IllegalArgumentException("Google authorization code is required");
        }

        String safeClientId = normalizeGoogleClientId(clientId);
        String safeClientSecret = normalizeGoogleConfigValue(clientSecret);
        String safeRedirectUri = normalizeGoogleConfigValue(redirectUri);

        if (safeClientId.isBlank() || safeClientSecret.isBlank()) {
            throw new IllegalArgumentException("Google OAuth client credentials are not configured");
        }

        List<String> redirectUriCandidates = resolveRedirectUriCandidates(safeRedirectUri);

        GoogleTokenResponse tokenResponse = null;
        IOException lastExchangeFailure = null;

        for (String redirectUri : redirectUriCandidates) {
            try {
                tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        GsonFactory.getDefaultInstance(),
                        safeClientId,
                        safeClientSecret,
                        authCode,
                        redirectUri)
                        .execute();
                break;
            } catch (Exception e) {
                lastExchangeFailure = new IOException(
                        "Failed to exchange Google OAuth authorization code with redirect URI: "
                                + redirectUri,
                        e);
            }
        }

        if (tokenResponse == null) {
            throw lastExchangeFailure != null
                    ? lastExchangeFailure
                    : new IOException("Failed to exchange Google OAuth authorization code");
        }

        if (tokenResponse.getRefreshToken() == null || tokenResponse.getRefreshToken().isBlank()) {
            throw new IllegalArgumentException("Google returned no refresh token for the supplied authorization code");
        }

        GoogleCalendarToken token = tokenRepo.findByUserId(userId).orElseGet(GoogleCalendarToken::new);
        token.setUserId(userId);
        token.setRefreshToken(tokenResponse.getRefreshToken());
        token.setAccessToken(tokenResponse.getAccessToken());
        token.setAccessTokenExpiry(Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds()));
        token.setConnectedAt(Instant.now());
        tokenRepo.save(token);
    }

    /**
     * Creates Google Calendar events for both the helper and the requester once a
     * task has been accepted and the transaction has been committed.
     *
     * @param event the task acceptance event containing the task and participant IDs
     */
    @TransactionalEventListener( phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
    public void onTaskAccepted(TaskAcceptedEvent event) {
        TaskInvoice task = taskInvoiceRepository.findById(event.getTaskId()).orElse(null);
        if (task == null) {
            return;
        }

        String helperEventId = createEventFor(event.getHelperId(), task);
        String dependentEventId = createEventFor(event.getRequesterId(), task);

        boolean changed = false;
        if (helperEventId != null) {
            task.setGoogleCalendarEventIdHelper(helperEventId);
            changed = true;
        }
        if (dependentEventId != null) {
            task.setGoogleCalendarEventIdDependent(dependentEventId);
            changed = true;
        }
        if (changed) {
            taskInvoiceRepository.save(task);
        }
    }

    /**
     * Creates a single Google Calendar event for the given user using the stored
     * OAuth credentials and the task details.
     *
     * @param userId the user ID whose calendar should receive the event
     * @param task the task to convert into a calendar event
     * @return the ID of the newly created event, or null if creation fails
     */
    private String createEventFor(int userId, TaskInvoice task){
        Optional<GoogleCalendarToken> tokenOpt = tokenRepo.findByUserId(userId);

        if(tokenOpt.isEmpty()) {
            LOG.warn("No Google Calendar token for user {}, skipping event creation", userId);
            return null;
        }

        try{
            Calendar calendarService = buildCalendarClient(tokenOpt.get());
            Event created = calendarService.events().insert("primary", buildEvent(task)).execute();
            return created.getId();
        }catch (Exception e) {
            LOG.error("Failed to create calendar event for user {} on task {}: {}",
                userId, task.getTaskid(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Builds an authenticated Google Calendar client for the provided user token.
     *
     * @param token the Google Calendar token containing the refresh token
     * @return an authenticated calendar client ready for API calls
     * @throws Exception if the client cannot be created or refreshed
     */
    private Calendar buildCalendarClient(GoogleCalendarToken token) throws Exception {
        String safeClientId = normalizeGoogleConfigValue(clientId);
        String safeClientSecret = normalizeGoogleConfigValue(clientSecret);

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .setClientSecrets(safeClientId, safeClientSecret)
                .build()
                .setRefreshToken(token.getRefreshToken());

        credential.refreshToken(); // exchanges the stored refresh token for a fresh access token

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName(APP_NAME)
                .build();
    }

    /**
     * Builds the Google Calendar event payload from task data.
     *
     * @param task the task details to populate into the event
     * @return the event payload with summary, description, and date/time values
     */
    private Event buildEvent(TaskInvoice task) {
        LocalDate startDate = task.getStartdate();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalDate endDate = task.getEnddate() != null ? task.getEnddate() : startDate;

        DateTime start = new DateTime(
                Date.from(startDate.atTime(startTime).atZone(ZoneId.of(TIMEZONE)).toInstant()));
        DateTime end = new DateTime(
                Date.from(endDate.atTime(startTime.plusHours(1)).atZone(ZoneId.of(TIMEZONE)).toInstant()));

        return new Event()
                .setSummary(task.getTitle())
                .setDescription(task.getInstructions())
                .setStart(new EventDateTime().setDateTime(start).setTimeZone(TIMEZONE))
                .setEnd(new EventDateTime().setDateTime(end).setTimeZone(TIMEZONE));
    }
}
