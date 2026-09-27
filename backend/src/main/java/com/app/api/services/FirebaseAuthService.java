package com.app.api.services;

import org.springframework.stereotype.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.GetUsersResult;
import com.google.firebase.auth.UserIdentifier;
import com.google.firebase.auth.UserRecord;
import com.app.api.repositories.UserRepository;
import com.app.api.repositories.SettingsRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import java.util.Map;

/**
 * Service responsible for interacting with Firebase Authentication.
 * <p>
 * Provides functionality for verifying Firebase ID tokens and retrieving
 * the authenticated user's information.
 * </p>
 */
@Service
public class FirebaseAuthService {

    
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;


    /**
    * Creates a new Firebase authentication service.
    *
    * @param userRepository repository used to retrieve application users
    */
    public FirebaseAuthService(UserRepository userRepository, SettingsRepository settingsRepository) {
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
    }

    /**
     * Verifies a Firebase ID token.
     *
     * @param idToken the Firebase ID token to verify
     * @return the decoded {@code FirebaseToken} if the token is valid
     * @throws FirebaseAuthException if the token is invalid, expired,
     *         or cannot be verified
     */
    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken, true);
    }

    /**
     * Resolves the integer user_id from user_table for the given Firebase ID token.
     * Verifies the token, extracts the Firebase UID, then looks up the matching row.
     *
     * @param idToken the Firebase ID token from the Authorization header
     * @return the integer user_id from user_table
     * @throws FirebaseAuthException if the token is invalid or expired
     * @throws RuntimeException if no user_table row exists for the Firebase UID
     */
    public int getUserIdFromToken(String idToken) throws FirebaseAuthException{
        FirebaseToken decoded = verifyIdToken(idToken);
        String firebaseUid = decoded.getUid();
        
        int userId = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("No user found for FirebaseUID: " + firebaseUid))
                .getUserid();

        settingsRepository.findById(userId).ifPresent(settings ->{
            settings.setLastSeen(Instant.now());
            settingsRepository.save(settings);
        });
        
        return userId;
    }

    /**
     * Revokes all refresh tokens for a user, effectively logging them out
     * of all devices/sessions. Existing ID tokens remain valid until they
     * expire naturally, unless checkRevoked is used during verification.
     *
     * @param uid the Firebase UID of the user to log out
     * @throws FirebaseAuthException if the UID is invalid or revocation fails
     */
    public void revokeUserSessions(String uid) throws FirebaseAuthException {
        FirebaseAuth.getInstance().revokeRefreshTokens(uid);
    }

    /**
     * Resolves account-creation timestamps for a batch of Firebase UIDs, for
     * the 2.8 sudden_spike check. Firebase's getUsers call caps at 100
     * identifiers per request, so this chunks transparently. A UID Firebase
     * can't resolve (deleted account, bad data) is simply omitted from the
     * result rather than failing the whole batch — callers should treat a
     * missing entry as "can't determine account age, skip this candidate."
     *
     * @param firebaseUids the UIDs to look up
     * @return uid to account-creation instant, for whichever UIDs resolved
     */
    public Map<String, Instant> getAccountCreationTimes(Collection<String> firebaseUids) throws FirebaseAuthException {
        Map<String, Instant> result = new HashMap<>();
        List<String> uidList = new ArrayList<>(firebaseUids);

        for (int start = 0; start < uidList.size(); start += 100) {
            List<String> chunk = uidList.subList(start, Math.min(start + 100, uidList.size()));
            // build identifiers explicitly rather than via the cast above:
            List<UserIdentifier> ids = new ArrayList<>(chunk.size());
            for (String uid : chunk) {
                ids.add(new com.google.firebase.auth.UidIdentifier(uid));
            }

            GetUsersResult batch = FirebaseAuth.getInstance().getUsers(ids);
            for (UserRecord record : batch.getUsers()) {
                long creationMillis = record.getUserMetadata().getCreationTimestamp();
                result.put(record.getUid(), Instant.ofEpochMilli(creationMillis));
            }
        }
        return result;
    }
}   
