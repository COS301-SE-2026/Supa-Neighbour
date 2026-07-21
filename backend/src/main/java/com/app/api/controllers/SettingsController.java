package com.app.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.ShowStatusResponse;
import com.app.api.dtos.ShowStatusRequest;
import com.app.api.dtos.UserStatusResponse;


import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;

import com.app.api.services.SettingsServices;

/** 
 * REST controller for the Settings table
*/
@RestController
@RequestMapping("api/settings")
public class SettingsController {
    
    private final FirebaseAuthService firebaseAuthService;
    private final SettingsServices settingsServices;

    public SettingsController(FirebaseAuthService firebaseAuthService, SettingsServices settingsServices){
        this.firebaseAuthService = firebaseAuthService;
        this.settingsServices = settingsServices;
    }


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

    @PostMapping("/users/me/show-status")
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

}
