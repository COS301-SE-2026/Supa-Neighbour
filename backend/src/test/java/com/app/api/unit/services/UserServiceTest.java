package com.app.api.unit.services;

import com.app.api.models.User;
import com.app.api.repositories.UserRepository;
import com.app.api.services.UserService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



public class UserServiceTest
{

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void initMocks()
    {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void getUserById_success()
    {
        User user = new User();
        user.setid(101);
        when(userRepo.findById(101)).thenReturn(Optional.of(user));

        User usrfound = userService.getUserById(101);

        assertNotNull(usrfound);
        assertEquals(101, usrfound.getUserid());
        verify(userRepo, times(1)).findById(101);
    }


    @Test
    void getUserById_returnsNull() 
    {
        when(userRepo.findById(999)).thenReturn(Optional.empty());

        User dne = userService.getUserById(999);

        assertNull(dne);
        verify(userRepo, times(1)).findById(999);
    }


    @Test
    void getAllUsers_success()
    {
        User user1 = new User();
        user1.setid(101);

        User user2 = new User();
        user2.setid(102);

        when(userRepo.findAll()).thenReturn(List.of(user1, user2));

        Iterable<User> usrs = userService.getAllUsers();

        assertNotNull(usrs);
        verify(userRepo, times(1)).findAll();
    }


    @Test
    void saveUser_success()
    {
        User user = new User();
        user.setid(201);
        when(userRepo.save(user)).thenReturn(user);

        User newUser = userService.saveUser(user);

        assertNotNull(newUser);
        assertEquals(201, newUser.getUserid());
        verify(userRepo, times(1)).save(user);
    }
}
