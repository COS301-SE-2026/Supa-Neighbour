package com.app.api.controllers;

import com.app.api.dtos.UserProfileResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.UserProfileService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.ResponseEntity;
import com.app.api.dtos.UpdateProfileRequest;
import com.app.api.dtos.UpdateProfileResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * REST controller that provides endpoints for retrieving and
 * updating the authenticated user's profile.
 */
@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final FirebaseAuthService firebaseAuthService;


     /**
     * Constructs a {@code UserProfileController} with the required services.
     *
     * @param userProfileService service responsible for retrieving and updating
     *                           user profile information
     * @param firebaseAuthService service used to authenticate Firebase tokens
     *                            and retrieve the associated user ID
     */
    private UserProfileController(UserProfileService userProfileService, FirebaseAuthService firebaseAuthService){
        this.userProfileService = userProfileService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Retrieves the profile of the authenticated user.
     *
     * <p>The Firebase authentication token is extracted from the
     * {@code Authorization} header and validated before the user's
     * profile is returned. Helper-specific fields are {@code null}
     * for users who are not registered as helpers.</p>
     *
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @return a {@link ResponseEntity} containing the authenticated user's
     *         profile if the token is valid, or a 401 Unauthorized response
     *         if the token is invalid or expired
     */
    @GetMapping("profile")
    public ResponseEntity<?> getMyProfile(
        @RequestHeader("Authorization") String authHeader
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            UserProfileResponse response = userProfileService.getProfile(userId);
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Updates the profile of the authenticated user.
     *
     * <p>The Firebase authentication token is extracted from the
     * {@code Authorization} header and validated before the supplied
     * profile information is applied.</p>
     *
     * @param request the profile fields to update
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @return a {@link ResponseEntity} containing the updated profile
     *         information if the request is successful, or a 401
     *         Unauthorized response if the token is invalid or expired
     */
    @PatchMapping("profile")
    public ResponseEntity<?> updateProfile(
        @RequestBody UpdateProfileRequest request,
        @RequestHeader("Authorization") String authHeader
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            UpdateProfileResponse response = userProfileService.updateProfile(userId, request);
            System.out.println("Leaving controller successfully");
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}
