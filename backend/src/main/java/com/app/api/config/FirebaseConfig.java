package com.app.api.config;


import java.io.IOException;
import java.io.InputStream;

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
        try{
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("Firebase/serviceAccountKey.json");
        
            FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

            if(FirebaseApp.getApps().isEmpty()){
                FirebaseApp.initializeApp(options);
            }
        }
        catch(Exception e) {
        // Handle the exception, e.g., log it or rethrow it
        throw new RuntimeException("Failed to initialize Firebase", e);
    }
}
}
