package com.app.api.controllers;

import com.app.api.dtos.HelperProfileResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.HelperProfileService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/helpers")
public class HelperProfileController {
    private final HelperProfileService helperProfileService;
    private final FirebaseAuthService firebaseAuthService;

    public HelperProfileController(HelperProfileService helperProfileService, FirebaseAuthService firebaseAuthService){
        this.helperProfileService = helperProfileService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * GET /api/helpers/{helperId}/profile
     *
     * Returns the public profile of a helper — safe to call by any
     * authenticated user (requester or helper). Excludes exact address
     * and contact details (R4.1.2).
     */

    @GetMapping("{helperId}/profile")
    public ResponseEntity<?> getHelperProfile(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable int helperId
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.verifyIdToken(token);
            HelperProfileResponse response = helperProfileService.getProfile(helperId);
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase Token");
        }
    }
}
