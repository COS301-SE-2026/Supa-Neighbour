package com.app.api.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {
    /**
     * Initializes the Firebase application with the default configuration.
     *
     * @return the initialized FirebaseApp instance
     */
    @Bean
    public com.google.firebase.FirebaseApp firebaseApp() {
        try {
            return com.google.firebase.FirebaseApp.getInstance();
        } catch (IllegalStateException e) {
            // Initialize Firebase if it hasn't been initialized yet
            com.google.firebase.FirebaseOptions options = com.google.firebase.FirebaseOptions.builder()
                    .setCredentials(com.google.auth.oauth2.GoogleCredentials.getApplicationDefault())
                    .build();
            return com.google.firebase.FirebaseApp.initializeApp(options);
        }
    }
}
