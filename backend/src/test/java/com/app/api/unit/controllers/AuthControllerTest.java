package com.app.api.unit.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.app.api.controllers.AuthController;
import com.app.api.dtos.RegisterRequest;
import com.app.api.models.Address;
import com.app.api.models.Badges;
import com.app.api.models.Dependent;
import com.app.api.models.HelperAnalytics;
import com.app.api.models.Ratings;
import com.app.api.models.Settings;
import com.app.api.models.User;
import com.app.api.models.UserAchievement;
import com.app.api.repositories.AddressRepository;
import com.app.api.repositories.BadgesRepository;
import com.app.api.repositories.DependentRepository;
import com.app.api.repositories.HelperAnalyticsRepository;
import com.app.api.repositories.HelperRepository;
import com.app.api.repositories.RatingsRepository;
import com.app.api.repositories.SettingsRepository;
import com.app.api.repositories.UserAchievementRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.security.AuthenticatedUser;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.ModerationActionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.app.api.models.Helper;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private FirebaseAuthService firebaseAuthService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private SettingsRepository settingsRepository;

    @Mock
    private HelperRepository helperRepository;

    @Mock
    private DependentRepository dependentRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private HelperAnalyticsRepository helperAnalyticsRepository;

    @Mock
    private BadgesRepository badgesRepository;

    @Mock
    private RatingsRepository ratingsRepository;

    @InjectMocks
    private AuthController authController;

    @Mock
    private ModerationActionService moderationActionService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String BEARER_TOKEN = "Bearer valid-token";
    private static final String RAW_TOKEN = "valid-token";
    private static final int DEFAULT_RATING_ID = 6;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    private RegisterRequest buildValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setAddressId(1);
        request.setFirstName("Thabo");
        request.setLastName("Nkosi");
        request.setUsername("thabon");
        request.setPhoneNumber("0821234567");
        request.setDateOfBirth(java.sql.Date.valueOf(LocalDate.of(1995, 5, 20)));
        request.setGender("Male");
        request.setUserType("Dependent");
        return request;
    }

    @Test
void registerUser_WhenNewUser_ReturnsOkWithUser() throws Exception {
    try {
        RegisterRequest request = buildValidRegisterRequest();

        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("firebase-uid-1");
        when(decodedToken.getEmail()).thenReturn("thabon@example.com");

        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.empty());

        Address address = new Address();
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));

        Ratings defaultRating = new Ratings();
        when(ratingsRepository.findById(DEFAULT_RATING_ID)).thenReturn(Optional.of(defaultRating));

        Badges badge = new Badges();
        badge.setXpReward(1000);
        when(badgesRepository.findAll()).thenReturn(List.of(badge));
        when(userAchievementRepository.save(any(UserAchievement.class))).thenReturn(new UserAchievement());

        User savedUser = new User();
        savedUser.setFirstName("Thabo");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(settingsRepository.save(any(Settings.class))).thenReturn(new Settings());
        when(helperRepository.save(any())).thenReturn(new Helper());
        when(dependentRepository.save(any())).thenReturn(new Dependent());
        when(helperAnalyticsRepository.existsById(anyString())).thenReturn(false);
        when(helperAnalyticsRepository.save(any(HelperAnalytics.class))).thenReturn(new HelperAnalytics());

        mockMvc.perform(post("/api/auth/register")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userRepository, times(1)).save(any(User.class));
        verify(settingsRepository, times(1)).save(any(Settings.class));
        verify(userAchievementRepository, times(1)).save(any(UserAchievement.class));
        verify(helperRepository, times(1)).save(any());
        verify(dependentRepository, times(1)).save(any());
        verify(helperAnalyticsRepository, times(1)).save(any(HelperAnalytics.class));
    } catch (Exception e) {
        e.printStackTrace(); // This will show the exact line causing the NullPointerException
        throw e;
    }
}

    @Test
    void registerUser_WhenUserAlreadyExists_ReturnsConflict() throws Exception {

        RegisterRequest request = buildValidRegisterRequest();

        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("firebase-uid-1");

        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/auth/register")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(userRepository, never()).save(any(User.class));
        verify(settingsRepository, never()).save(any(Settings.class));
    }

    @Test
    void registerUser_WhenAddressNotFound_ThrowsException() throws FirebaseAuthException {

        RegisterRequest request = buildValidRegisterRequest();

        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("firebase-uid-1");

        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.empty());
        when(addressRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> mockMvc.perform(post("/api/auth/register")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))));
    }

    @Test
    void login_WhenUserExists_ReturnsUser() throws Exception {
        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("firebase-uid-1");
        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);

        User user = new User();
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        
        // ADD THESE MOCKS:
        when(moderationActionService.isBanned(user)).thenReturn(false);
        when(moderationActionService.isSuspended(user)).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userRepository, times(1)).findByFirebaseUid("firebase-uid-1");
    }

    @Test
    void login_WhenUserNotFound_ThrowsException() throws FirebaseAuthException {

        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("unknown-uid");

        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);
        when(userRepository.findByFirebaseUid("unknown-uid")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> mockMvc.perform(post("/api/auth/login")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)));
    }

    @Test
    void getProfile_ReturnsAuthenticatedUser() throws Exception {

        User user = new User();
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
        when(authenticatedUser.getUser()).thenReturn(user);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);

        mockMvc.perform(get("/api/auth/profile")
                .principal(authentication)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(authenticatedUser, times(1)).getUser();
    }

    @Test
    void logout_WhenTokenValid_ReturnsOk() throws Exception {

        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("firebase-uid-1");
        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);
        doNothing().when(firebaseAuthService).revokeUserSessions(anyString());

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));

        verify(firebaseAuthService, times(1)).revokeUserSessions("firebase-uid-1");
    }

    @Test
    void logout_WhenTokenInvalid_ReturnsUnauthorized() throws Exception {

        FirebaseAuthException authException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenThrow(authException);

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isUnauthorized());

        verify(firebaseAuthService, never()).revokeUserSessions(anyString());
    }

    @Test
    void registerUser_WhenInvalidToken_ReturnsUnauthorized() throws Exception {
        RegisterRequest request = buildValidRegisterRequest();
        
        when(firebaseAuthService.verifyIdToken(RAW_TOKEN))
            .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(post("/api/auth/register")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerUser_WhenRatingNotFound_ThrowsException() throws FirebaseAuthException {
        RegisterRequest request = buildValidRegisterRequest();
        
        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("firebase-uid-1");
        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.empty());
        
        Address address = new Address();
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(ratingsRepository.findById(DEFAULT_RATING_ID)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> mockMvc.perform(post("/api/auth/register")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))));
    }

    @Test
    void adminLogin_WhenUserIsAdmin_ReturnsOk() throws Exception {
        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("admin-uid");
        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);

        User adminUser = new User();
        adminUser.setIsAdmin(true);
        when(userRepository.findByFirebaseUid("admin-uid")).thenReturn(Optional.of(adminUser));

        mockMvc.perform(post("/api/auth/admin/login")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void adminLogin_WhenUserIsNotAdmin_ReturnsForbidden() throws Exception {
        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("user-uid");
        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);

        User regularUser = new User();
        regularUser.setIsAdmin(false);
        when(userRepository.findByFirebaseUid("user-uid")).thenReturn(Optional.of(regularUser));

        mockMvc.perform(post("/api/auth/admin/login")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Not an admin"));
    }

    @Test
    void login_WhenUserIsBanned_ReturnsForbidden() throws Exception {
        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("firebase-uid-1");
        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);

        User user = new User();
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        
        when(moderationActionService.isBanned(user)).thenReturn(true);
        //when(moderationActionService.isSuspended(user)).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Your account has been permanently banned"));
    }

    @Test
    void login_WhenUserIsSuspended_ReturnsForbidden() throws Exception {
        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("firebase-uid-1");
        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);

        User user = new User();
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        
        when(moderationActionService.isBanned(user)).thenReturn(false);
        when(moderationActionService.isSuspended(user)).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Your account is currently suspended"));
    }

    @Test
    void login_WhenUserIsBannedAndSuspended_ReturnsForbiddenWithBanMessage() throws Exception {
        
        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("firebase-uid-1");
        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);

        User user = new User();
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        
        when(moderationActionService.isBanned(user)).thenReturn(true);
        //when(moderationActionService.isSuspended(user)).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Your account has been permanently banned"));
}
}
