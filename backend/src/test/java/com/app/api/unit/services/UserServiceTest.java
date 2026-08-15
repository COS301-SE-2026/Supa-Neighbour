package com.app.api.unit.services;

import com.app.api.models.Address;
import com.app.api.models.Badges;
import com.app.api.models.Ratings;
import com.app.api.models.User;
import com.app.api.repositories.UserRepository;
import com.app.api.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void getAllUsers_returnsAllUsers() {
        User user1 = new User();
        user1.setUserid(1);

        User user2 = new User();
        user2.setUserid(2);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(user1, result.get(0));
        assertEquals(user2, result.get(1));
    }

    @Test
    void getUserById_whenFound_returnsUser() {
        User user = new User();
        user.setUserid(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertEquals(user, result);
    }

    @Test
    void getUserById_whenNotFound_returnsNull() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        User result = userService.getUserById(99);

        assertNull(result);
    }

    @Test
    void saveUser_whenNull_returnsNull() {
        User result = userService.saveUser(null);

        assertNull(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_whenValid_savesAndReturnsUser() {
        User user = new User();
        user.setUserid(1);

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.saveUser(user);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_whenFound_updatesAllFieldsAndReturnsSaved() {
        User existing = new User();
        existing.setUserid(1);

        Badges badge = mock(Badges.class);
        Address address = mock(Address.class);
        Ratings rating = mock(Ratings.class);
        Date dob = Date.valueOf("1990-01-01");

        User updated = new User();
        updated.setBadgeid(badge);
        updated.setAddressid(address);
        updated.setDateOfBirth(dob);
        updated.setEmail("new@example.com");
        updated.setFirstName("NewFirst");
        updated.setGender("F");
        updated.setLastName("NewLast");
        updated.setPhoneNumber("1234567890");
        updated.setRatingid(rating);
        updated.setUserType("ADMIN");

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User result = userService.updateUser(1, updated);

        assertEquals(existing, result);
        assertEquals(badge, existing.getBadgeid());
        assertEquals(address, existing.getAddressid());
        assertEquals(dob, existing.getDateOfBirth());
        assertEquals("new@example.com", existing.getEmail());
        assertEquals("NewFirst", existing.getFirstName());
        assertEquals("F", existing.getGender());
        assertEquals("NewLast", existing.getLastName());
        assertEquals("1234567890", existing.getPhoneNumber());
        assertEquals(rating, existing.getRatingid());
        assertEquals("ADMIN", existing.getUserType());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_whenNotFound_returnsNull() {
        User updated = new User();
        updated.setFirstName("NewFirst");

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        User result = userService.updateUser(99, updated);

        assertNull(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_callsRepositoryDeleteById() {
        userService.deleteUser(1);

        verify(userRepository).deleteById(1);
    }
}