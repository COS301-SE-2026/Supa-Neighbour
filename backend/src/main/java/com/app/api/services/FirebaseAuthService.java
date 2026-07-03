package com.app.api.services;

import org.springframework.stereotype.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

/**
 * Service responsible for interacting with Firebase Authentication.
 * <p>
 * Provides functionality for verifying Firebase ID tokens and retrieving
 * the authenticated user's information.
 * </p>
 */
@Service
public class FirebaseAuthService {
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
}
