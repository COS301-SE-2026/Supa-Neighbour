package com.app.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.app.api.models.User;
import com.app.api.repositories.UserRepository;

/**
 * Service layer for managing user operations.
 * Provides CRUD functionality for User entities.
 */
@Service
public class UserService {

    /** The user repository. */
    @Autowired
    private UserRepository userRepository;

    // Get all
    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a user by their identifier.
     *
     * @param id the user identifier
     * @return the user if found, or null if no user exists with the given id
     */
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Retrieves a user by their identifier.
     *
     * @param id the user identifier
     * @return the user if found, or null if no user exists with the given id
     */
    public User saveUser(User user) {
        if(user == null) {
            return null;
        }

        return userRepository.save(user);
    }

    // Update
    /**
     * Updates an existing user with the provided details.
     *
     * @param id      the identifier of the user to update
     * @param updated the user object containing the updated fields
     * @return the updated user, or null if no user exists with the given id
     */
    public User updateUser(int id, User updated) {
        User existing = userRepository.findById(id).orElse(null);
     
        if (existing == null) {
            return null;
        }
        
        existing.setBadgeid(updated.getBadgeid());
        existing.setAddressid(updated.getAddressid());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setEmail(updated.getEmail());
        existing.setFirstName(updated.getFirstName());
        existing.setGender(updated.getGender());
        existing.setLastName(updated.getLastName());
        existing.setPassword(updated.getPassword());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setRatingid(updated.getRatingid());
        existing.setUserType(updated.getUserType());

        return userRepository.save(existing);
    }

    // Delete
    
    /**
     * Deletes a user by their identifier.
     *
     * @param id the identifier of the user to delete
     */
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
