package com.app.api.services;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
import org.springframework.transaction.event.TransactionalEventListener;
import com.app.api.events.TaskAcceptedEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class GoogleCalenderTokenService {
    private static final String TIMEZONE = "Africa/Johannesburg";
    private static final String APP_NAME = "Supa-Neighbour";

    private final GoogleCalenderTokenRepository tokenRepo;
    private final TaskInvoiceRepository taskInvoiceRepository;

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
    /**
     * Exchanges a Google OAuth authorization code for a fresh token pair and stores
     * the resulting credentials for the specified user.
     *
     * @param userId the ID of the user connecting their Google Calendar account
     * @param authCode the authorization code returned by Google after consent
     * @throws IOException if the token exchange request fails
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
            return null;
        }

        try{
            Calendar calendarService = buildCalendarClient(tokenOpt.get());
            Event created = calendarService.events().insert("primary", buildEvent(task)).execute();
            return created.getId();
        }catch (Exception e) {
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
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .setClientSecrets(clientId, clientSecret)
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
