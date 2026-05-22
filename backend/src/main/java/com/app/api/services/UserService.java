package com.app.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.User;
import com.app.api.repositories.UserRepository;

/**
 * User service.
 */
@Service
public class UserService {

    /** The user repository. */
    @Autowired
    private UserRepository userRepository;

    /**
     * Get all users.
     * @return all users
     */
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by id.
     * @param id user id
     * @return user
     */
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Save user.
     * @param user user
     * @return saved user
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
