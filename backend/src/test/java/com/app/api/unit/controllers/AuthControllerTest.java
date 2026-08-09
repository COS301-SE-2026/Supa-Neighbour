package com.app.api.unit.controllers;

import com.app.api.controllers.AuthController;
import com.app.api.dtos.RegisterRequest;
import com.app.api.models.Address;
import com.app.api.models.Badges;
import com.app.api.models.Ratings;
import com.app.api.models.Settings;
import com.app.api.models.User;
import com.app.api.repositories.AddressRepository;
import com.app.api.repositories.BadgesRepository;
import com.app.api.repositories.RatingsRepository;
import com.app.api.repositories.SettingsRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.security.AuthenticatedUser;
import com.app.api.services.FirebaseAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link AuthController}.
 * <p>
 * NOTE: {@link RegisterRequest} field types for {@code dateOfBirth}, {@code gender}
 * and {@code userType} were not visible in the uploaded source, so this test assumes
 * {@code dateOfBirth} is a {@link LocalDate} and {@code gender}/{@code userType} are
 * {@link String}. Adjust the setters in {@link #buildValidRegisterRequest()} if the
 * real DTO differs.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private FirebaseAuthService firebaseAuthService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private BadgesRepository badgeRepository;

    @Mock
    private RatingsRepository ratingRepository;

    @Mock
    private SettingsRepository settingsRepository;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String BEARER_TOKEN = "Bearer valid-token";
    private static final String RAW_TOKEN = "valid-token";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    private RegisterRequest buildValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setAddressId(1);
        request.setBadgeId(1);
        request.setRatingId(1);
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

        RegisterRequest request = buildValidRegisterRequest();

        FirebaseToken decodedToken = mock(FirebaseToken.class);
        when(decodedToken.getUid()).thenReturn("firebase-uid-1");
        when(decodedToken.getEmail()).thenReturn("thabon@example.com");

        when(firebaseAuthService.verifyIdToken(RAW_TOKEN)).thenReturn(decodedToken);
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.empty());

        Address address = new Address();
        Badges badge = new Badges();
        Ratings rating = new Ratings();

        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(badgeRepository.findById(1)).thenReturn(Optional.of(badge));
        when(ratingRepository.findById(1)).thenReturn(Optional.of(rating));

        User savedUser = new User();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(settingsRepository.save(any(Settings.class))).thenReturn(new Settings());

        mockMvc.perform(post("/api/auth/register")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userRepository, times(1)).save(any(User.class));
        verify(settingsRepository, times(1)).save(any(Settings.class));
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
}