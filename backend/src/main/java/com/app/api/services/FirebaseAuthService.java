package com.app.api.services;

import org.springframework.stereotype.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.app.api.repositories.UserRepository;
import com.app.api.repositories.SettingsRepository;

import java.time.Instant;
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
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
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
}   
