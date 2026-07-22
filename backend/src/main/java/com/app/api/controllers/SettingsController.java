package com.app.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.ModeResponse;
import com.app.api.dtos.ShowStatusRequest;
import com.app.api.dtos.ShowStatusResponse;
import com.app.api.dtos.UserStatusResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.SettingsServices;
import com.google.firebase.auth.FirebaseAuthException;

/**
 * REST controller responsible for managing user application settings.
 * <p>
 * Provides endpoints for retrieving and updating a user's visibility
 * status and application mode. All endpoints require a valid Firebase
 * authentication token supplied in the Authorization header.
 * </p>
 */
@RestController
@RequestMapping("api/settings")
public class SettingsController {
    
    private final FirebaseAuthService firebaseAuthService;
    private final SettingsServices settingsServices;


     /**
     * Constructs a new {@code SettingsController}.
     *
     * @param firebaseAuthService service used to authenticate Firebase tokens
     *                            and retrieve the authenticated user's ID
     * @param settingsServices    service responsible for retrieving and
     *                            updating user settings
     */
    public SettingsController(FirebaseAuthService firebaseAuthService, SettingsServices settingsServices){
        this.firebaseAuthService = firebaseAuthService;
        this.settingsServices = settingsServices;
    }


    /**
     * Retrieves the authenticated user's visibility status.
     *
     * @param authHeader the Firebase Bearer token contained in the
     *                   Authorization header
     * @return HTTP 200 containing the user's current visibility status,
     *         or HTTP 401 if the Firebase token is invalid or expired
     */
    @GetMapping("/users/show-status")
    public ResponseEntity<?> getStatus(
        @RequestHeader("Authorization") String authHeader
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            UserStatusResponse response = settingsServices.getUserStatus(userId);
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Updates the authenticated user's visibility status.
     *
     * @param authHeader the Firebase Bearer token contained in the
     *                   Authorization header
     * @param request    request containing the new visibility status
     * @return HTTP 200 containing the updated visibility status,
     *         or HTTP 401 if the Firebase token is invalid or expired
     */    
    @PostMapping("/users/show-status")
    public ResponseEntity<?> updateStatus(
        @RequestHeader("Authorization") String authHeader, 
        @RequestBody ShowStatusRequest request
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            ShowStatusResponse response = settingsServices.updateShowStatus(userId, request.getshowStatus());
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Retrieves the authenticated user's current application mode.
     *
     * @param authHeader the Firebase Bearer token contained in the
     *                   Authorization header
     * @return HTTP 200 containing the user's current application mode,
     *         or HTTP 401 if the Firebase token is invalid or expired
     */
    @GetMapping("/users/mode")
    public ResponseEntity<?> getMode(
        @RequestHeader("Authorization") String authHeader
    ){  
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            ModeResponse response = settingsServices.getUserMode(userId);
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Updates the authenticated user's application mode.
     *
     * @param authHeader the Firebase Bearer token contained in the
     *                   Authorization header
     * @param request    request containing the new application mode
     * @return HTTP 200 containing the updated application mode,
     *         or HTTP 401 if the Firebase token is invalid or expired
     */
    @PostMapping("/users/mode")
    public ResponseEntity<?> setMode(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody ModeResponse request
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            ModeResponse response = settingsServices.setUserMode(userId, request.getMode());
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

        /**
     * Retrieves the online status information for a specified user.
     * <p>
     * A valid Firebase authentication token is required to access this
     * endpoint. The authenticated user may retrieve the visibility and
     * online status information of another user, subject to that user's
     * visibility settings.
     * </p>
     *
     * @param authHeader the Firebase Bearer token contained in the
     *                   Authorization header
     * @param userId the unique identifier of the user whose status
     *               is being requested
     * @return HTTP 200 containing the user's status information,
     *         or HTTP 401 if the Firebase token is invalid or expired
     */
    @GetMapping("/users/{userId}/status")
    public ResponseEntity<?> getStatusForUser(
        @RequestHeader("Authorization") String authHeader, 
        @PathVariable int userId
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);
            UserStatusResponse response = settingsServices.getUserStatus(userId);
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    @GetMapping("/users/information/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable int userId, @RequestHeader("Authorization") String authHeader)
    {
        try{
            String token = authHeader.replace("Bearer ","");
            firebaseAuthService.getUserIdFromToken(token);
            UserStatusResponse response = settingsServices.getUserStatus(userId);
            return ResponseEntity.ok(response);
        }
        catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid Firebase Token");
        }
    }
}
