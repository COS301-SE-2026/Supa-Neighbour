package com.app.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.app.api.models.User;
import com.app.api.services.UserService;

/**
 * REST controller for user-related endpoints.
 */
@RestController
public class UserController {

    /** The user service. */
    private UserService userService;

    /**
     * Constructs a UserController with the given UserService.
     * @param userService the user service
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get a user by their ID.
     * @param id the user ID
     * @return the user if found, 404 otherwise
     */
    @GetMapping("api/user")
    public ResponseEntity<User> getUser(@RequestParam Integer id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
