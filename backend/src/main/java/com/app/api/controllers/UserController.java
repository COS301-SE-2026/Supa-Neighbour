package com.app.api.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.app.api.models.User;
import com.app.api.services.UserService;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class UserController {
    private UserService userService;

    /**
     * Returns all users.
     * 
     * @return list of users
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("api/user")
    public User getUser(@RequestParam Integer id) {
        // Call the service layer to get the user by ID
        return userService.getUserById(id);
    };

}
