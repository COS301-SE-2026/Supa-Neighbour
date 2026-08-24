package com.app.api.controllers;

import java.time.Instant;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.RegisterRequest;
import com.app.api.models.Address;
import com.app.api.models.Settings;
import com.app.api.models.Settings.ThemeMode;
import com.app.api.models.User;
import com.app.api.models.Helper;
import com.app.api.models.Dependent;
import com.app.api.repositories.AddressRepository;
import com.app.api.repositories.DependentRepository;
import com.app.api.repositories.HelperRepository;
import com.app.api.repositories.RatingsRepository;
import com.app.api.repositories.SettingsRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.security.AuthenticatedUser;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import com.app.api.models.Badges;
import com.app.api.models.UserAchievement;
import com.app.api.repositories.BadgesRepository;
import com.app.api.repositories.UserAchievementRepository;
import java.util.List;
import com.app.api.models.Ratings;
import com.app.api.models.HelperAnalytics;
import com.app.api.repositories.HelperAnalyticsRepository;

import jakarta.transaction.Transactional;
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
    private final SettingsRepository settingsRepository;
    private final HelperRepository helperRepository;
    private final DependentRepository dependentRepository;
    private final FirebaseAuthService firebaseAuthService;
    private final UserRepository userRepository;

    private final BadgesRepository badgesRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final HelperAnalyticsRepository helperAnalyticsRepository;
    
    private final RatingsRepository ratingsRepository;

    /** rating_id of the default "Unranked" tier assigned to new users on registration. */
    private static final int DEFAULT_RATING_ID = 6;

    /**
     * Creates a new authentication controller.
     *
     * @param firebaseAuthService the Firebase authentication service
     * @param userRepository the repository used to manage users
     */
    public AuthController(FirebaseAuthService firebaseAuthService,UserRepository userRepository,AddressRepository addressRepository, SettingsRepository settingsRepository, HelperRepository helperRepository, DependentRepository dependentRepository, BadgesRepository badgesRepository,UserAchievementRepository userAchievementRepository, RatingsRepository ratingsRepository, HelperAnalyticsRepository helperAnalyticsRepository) {
            this.firebaseAuthService = firebaseAuthService;
            this.userRepository = userRepository;
            this.addressRepository = addressRepository;
            this.settingsRepository = settingsRepository;
            this.helperRepository = helperRepository;
            this.dependentRepository = dependentRepository;
            this.badgesRepository = badgesRepository;
            this.userAchievementRepository = userAchievementRepository;
            this.ratingsRepository = ratingsRepository;
            this.helperAnalyticsRepository = helperAnalyticsRepository;
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
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestHeader("Authorization") String idToken,@RequestBody RegisterRequest request)
            throws FirebaseAuthException {
        FirebaseToken decodedToken;
        String token = idToken.replace("Bearer ", "");
        try{
            decodedToken = firebaseAuthService.verifyIdToken(token);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
        }
        

        if (userRepository.findByFirebaseUid(decodedToken.getUid()).isPresent()) {
            return ResponseEntity.status(HttpStatus.SC_CONFLICT)
                .body("User already exists");
        }

        Address address = addressRepository.findById(request.getAddressId())
            .orElseThrow(() -> new RuntimeException("Address not found"));


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

        Ratings defaultRating = ratingsRepository.findById(DEFAULT_RATING_ID)
            .orElseThrow(() -> new RuntimeException("Default rating tier not found"));
        user.setRatingid(defaultRating);

        user.setAddressid(address);

        User savedUser = userRepository.save(user);
        Settings defaultSettings = new Settings();

        defaultSettings.setUser(savedUser);
        defaultSettings.setLastSeen(Instant.now());
        defaultSettings.setShowStatus(true);
        defaultSettings.setShowPhoneNo(false);
        defaultSettings.setMode(ThemeMode.LIGHT);

        settingsRepository.save(defaultSettings);

        List<Badges> allBadges = badgesRepository.findAll();
        for(Badges badge: allBadges){
            UserAchievement userAchievement = new UserAchievement();
            userAchievement.setUserId(savedUser);
            userAchievement.setBadgeId(badge);
            userAchievement.setProgressCurrent(0);
            userAchievement.setProgressTarget(badge.getXpReward());
            userAchievement.setAwardedOn(null);
            userAchievementRepository.save(userAchievement);
        }

        if(!"Admin".equals(savedUser.getUserType())){
            Helper helper = new Helper();
            helper.setUserid(savedUser);
            helper.setHelperXp(0);
            helper.setAvailable(false);
            helperRepository.save(helper);

            String analyticsId = "HELPER_" + savedUser.getFirstName().toUpperCase();
            if(helperAnalyticsRepository.existsById(analyticsId)){
                analyticsId = analyticsId + "_" + savedUser.getUserid();
            }

            HelperAnalytics helperAnalytics = new HelperAnalytics();
            helperAnalytics.setHelperAnalyticsid(analyticsId);
            helperAnalytics.setUserid(savedUser);
            helperAnalytics.setAverageRating(0.0f);
            helperAnalyticsRepository.save(helperAnalytics);

            Dependent dependent = new Dependent();
            dependent.setUserId(savedUser);
            dependentRepository.save(dependent);
        }
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
        String token = idToken.replace("Bearer ", "");
        FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(token);

        User user = userRepository.findByFirebaseUid(decodedToken.getUid()).orElseThrow(() -> new RuntimeException("User not found"));

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
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader){
        try{
            String token = authHeader.replace("Bearer ", "");
            String uid = firebaseAuthService.verifyIdToken(token).getUid();
            firebaseAuthService.revokeUserSessions(uid);
            return ResponseEntity.ok("Logged out successfully");
        }catch(FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body(null);
        }
    }

    /**
     * Authenticates an admin user using a Firebase ID token.
     * <p>
     * The authentication flow consists of:
     * <ul>
     *     <li>Extracting and validating the Firebase ID token from the Authorization header</li>
     *     <li>Retrieving the user from the database using the Firebase UID</li>
     *     <li>Verifying that the user has admin privileges</li>
     * </ul>
     * </p>
     * 
     * @param authHeader the Authorization header containing the Bearer token
     *                   (format: "Bearer &lt;firebase-token&gt;")
     * @return a {@link ResponseEntity} containing:
     *         <ul>
     *             <li><b>200 OK</b> with the authenticated admin {@link User} if successful</li>
     *             <li><b>401 UNAUTHORIZED</b> with message "Invalid or expired token" 
     *                 if the Firebase token is invalid or expired</li>
     *             <li><b>404 NOT FOUND</b> with message "User not found" 
     *                 if no user exists with the given Firebase UID</li>
     *             <li><b>403 FORBIDDEN</b> with message "Not an admin" 
     *                 if the authenticated user does not have admin privileges</li>
     *         </ul>
     */
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestHeader("Authorization") String authHeader){
        User user;
        try{
            String token = authHeader.replace("Bearer ", "");
            FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(token);
            user = userRepository.findByFirebaseUid(decodedToken.getUid()).orElseThrow(() -> new RuntimeException("User not found"));
        } catch (FirebaseAuthException e) {
                return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("Invalid or expired token");
        } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("User not found");
        }
        if(!Boolean.TRUE.equals(user.getIsAdmin())){
            return ResponseEntity.status(HttpStatus.SC_FORBIDDEN).body("Not an admin");
        }

        return ResponseEntity.ok(user);
    }
}
