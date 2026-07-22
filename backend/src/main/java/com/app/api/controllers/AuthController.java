package com.app.api.controllers;

import org.apache.hc.core5.http.HttpStatus;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.hibernate.mapping.Map;
import com.app.api.dtos.RegisterRequest;
import com.app.api.models.Address;
import com.app.api.models.Badges;
import com.app.api.models.Ratings;
import com.app.api.models.User;
import com.app.api.repositories.AddressRepository;
import com.app.api.repositories.BadgesRepository;
import com.app.api.repositories.RatingsRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.security.AuthenticatedUser;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
/**
 * REST controller responsible for user authentication and account management.
 * <p>
 * This controller provides endpoints for:
 * <ul>
 *     <li>Registering a new user using a Firebase ID token.</li>
 *     <li>Logging in an existing user.</li>
 *     <li>Retrieving the authenticated user's profile.</li>
 * </ul>
 * Authentication is performed using Firebase Authentication, while user
 * information is persisted in the application's database.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AddressRepository addressRepository;
    private final BadgesRepository badgeRepository;
    private final RatingsRepository ratingRepository;
    /**
     * Service responsible for verifying Firebase ID tokens.
     */
    private final FirebaseAuthService firebaseAuthService;
    /**
     * Repository used to manage application users.
     */
    private final UserRepository userRepository;

    /**
     * Creates a new authentication controller.
     *
     * @param firebaseAuthService the Firebase authentication service
     * @param userRepository the repository used to manage users
     */
public AuthController(
        FirebaseAuthService firebaseAuthService,UserRepository userRepository,AddressRepository addressRepository,BadgesRepository badgeRepository,RatingsRepository ratingRepository) {
            this.firebaseAuthService = firebaseAuthService;
            this.userRepository = userRepository;
            this.addressRepository = addressRepository;
            this.badgeRepository = badgeRepository;
            this.ratingRepository = ratingRepository;
        }

    /**
     * Registers a new user using a Firebase ID token.
     * <p>
     * The provided Firebase token is verified, and if no existing user is found
     * with the corresponding Firebase UID, a new user is created and saved.
     * </p>
     *
     * @param idToken the Authorization header containing the Firebase Bearer token
     * @return a {@link ResponseEntity} containing the newly created user, or
     *         an error response if the user already exists
     * @throws FirebaseAuthException if the Firebase token is invalid or cannot be verified
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestHeader("Authorization") String idToken,@RequestBody RegisterRequest request)
            throws FirebaseAuthException {

        String token = idToken.replace("Bearer ", "");

        FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(token);

        if (userRepository.findByFirebaseUid(decodedToken.getUid()).isPresent()) {
            return ResponseEntity.status(HttpStatus.SC_CONFLICT)
                .body("User already exists");
        }

        Address address = addressRepository.findById(request.getAddressId())
            .orElseThrow(() -> new RuntimeException("Address not found"));

        Badges badge = badgeRepository.findById(request.getBadgeId())
            .orElseThrow(() -> new RuntimeException("Badge not found"));

        Ratings rating = ratingRepository.findById(request.getRatingId())
            .orElseThrow(() -> new RuntimeException("Rating not found"));

        User user = new User();

        user.setFirebaseUid(decodedToken.getUid());

        user.setEmail(decodedToken.getEmail());

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());


        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setUserType(request.getUserType());

        user.setAddressid(address);
        user.setBadgeid(badge);
        user.setRatingid(rating);

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    /**
     * Authenticates an existing user.
     * <p>
     * The Firebase ID token is verified and the corresponding application user
     * is retrieved from the database.
     * </p>
     *
     * @param idToken the Authorization header containing the Firebase Bearer token
     * @return a {@link ResponseEntity} containing the authenticated user
     * @throws FirebaseAuthException if the Firebase token cannot be verified
     * @throws RuntimeException if no user exists for the authenticated Firebase account
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String idToken) throws FirebaseAuthException {
        System.out.println("REGISTER ENDPOINT HIT");
        String token = idToken.replace("Bearer ", "");
        FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(token);

        User user =userRepository.findByFirebaseUid(decodedToken.getUid()).orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }
    
    /**
     * Returns the profile of the currently authenticated user.
     *
     * @param authentication the current Spring Security authentication object
     * @return the authenticated {@link User}
     */
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(authenticatedUser.getUser());
    } 

    // POST /api/auth/logout
/**
 * Logs the authenticated user out by revoking their Firebase refresh tokens.
 *
 * @param authHeader the Authorization header, expected as "Bearer <token>"
 * @return 200 OK on success, or 401 if the token is invalid
 */
@PostMapping("/logout")
public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader){

    try{
        String token = authHeader.replace("Bearer ", "");
        String Uid = firebaseAuthService.verifyIdToken(token).getUid();
        firebaseAuthService.revokeUserSessions(Uid);
        return ResponseEntity.ok(Map.of("message","Logged out successfully"));
    }catch(FirebaseAuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
}
