package com.app.api.services;

import org.springframework.stereotype.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.app.api.repositories.UserRepository;

/**
 * Service responsible for interacting with Firebase Authentication.
 * <p>
 * Provides functionality for verifying Firebase ID tokens and retrieving
 * the authenticated user's information.
 * </p>
 */
@Service
public class FirebaseAuthService {

    
    private  final UserRepository userRepository;


    public FirebaseAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("No user found for FirebaseUID: " + firebaseUid))
                .getUserid();
    }
}   