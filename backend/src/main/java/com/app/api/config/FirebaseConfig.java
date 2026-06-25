package com.app.api.config;


import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {
    /**
     * Initialises the Firebase Admin SDK on application startup.
     * Uses Application Default Credentials to authenticate with Firebase.
     * Skips initialisation if a FirebaseApp instance already exists.
     *
     * @throws IOException if the application default credentials cannot be loaded
     */
    @PostConstruct
    public void initialize()  throws IOException{
        if(FirebaseApp.getApps().isEmpty()){
            FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.getApplicationDefault()).build();
            FirebaseApp.initializeApp(options);
        }
    }
}
