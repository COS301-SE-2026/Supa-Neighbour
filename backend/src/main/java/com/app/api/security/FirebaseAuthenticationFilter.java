package com.app.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.app.api.services.FirebaseAuthService;
import com.app.api.repositories.UserRepository;
import com.app.api.models.User;
import com.google.firebase.auth.FirebaseToken;
import java.io.IOException;
import java.util.Collections;
import org.springframework.lang.NonNull;

/**
 * Spring Security filter responsible for authenticating incoming HTTP requests
 * using Firebase Authentication.
 * <p>
 * This filter extracts the Firebase ID token from the {@code Authorization}
 * header, verifies it with Firebase Authentication, retrieves the corresponding
 * application user from the database, and stores the authenticated user in the
 * Spring Security context.
 * </p>
 * <p>
 * Expected Authorization header format:
 * </p>
 * <pre>
 * Authorization: Bearer &lt;Firebase_ID_Token&gt;
 * </pre>
 */
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Service responsible for Firebase authentication operations.
     */
    private final FirebaseAuthService firebaseAuthService;
    /**
     * Repository used to retrieve application users.
     */
    private final UserRepository userRepository;

    /**
     * Creates a new Firebase authentication filter.
     *
     * @param firebaseAuthService the Firebase authentication service
     * @param userRepository the repository used to retrieve users by Firebase UID
     */
    public FirebaseAuthenticationFilter(FirebaseAuthService firebaseAuthService, UserRepository userRepository) {
        this.firebaseAuthService = firebaseAuthService;
        this.userRepository = userRepository;
    }

    /**
     * Authenticates incoming requests using the Firebase ID token provided in
     * the {@code Authorization} header.
     * <p>
     * If the token is valid and a matching user exists in the application
     * database, an {@link AuthenticatedUser} is created and stored in the
     * Spring Security context. If token verification fails, the request
     * is rejected with a {@code 401 Unauthorized} response.
     * </p>
     *
     * @param request the incoming HTTP request
     * @param response the outgoing HTTP response
     * @param filterChain the remaining filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs while processing the request
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) 
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try{
                String idToken = authorizationHeader.substring(7);
                FirebaseToken firebaseToken = firebaseAuthService.verifyIdToken(idToken);
                String firebaseUid = firebaseToken.getUid();
                User newUser = userRepository.findByFirebaseUid(firebaseUid).orElse(null);
                if(newUser != null){
                    AuthenticatedUser authenticatedUser = new AuthenticatedUser(newUser);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, Collections.emptyList());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }catch(Exception e){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        }
        filterChain.doFilter(request, response);
        
    }
    
}
