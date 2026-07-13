package com.app.api.services;

import org.springframework.stereotype.Service;
import java.util.List;
import com.app.api.models.User;
import com.app.api.repositories.UserRepository;

/**
 * Service responsible for managing user-related business logic.
 * <p>
 * Provides methods for creating, retrieving, updating, and deleting users.
 * </p>
 */
@Service
public class UserService {

    /**
     * Repository used to perform database operations on users.
     */

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all users.
     *
     * @return a list containing all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the ID of the user to retrieve
     * @return the user if found; otherwise {@code null}
     */
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

     /**
     * Saves a new user.
     *
     * @param user the user to save
     * @return the saved user, or {@code null} if the supplied user is
     *         {@code null}
     */
    public User saveUser(User user) {
        if(user == null) {
            return null;
        }
        return userRepository.save(user);
    }

    /**
     * Updates an existing user's information.
     *
     * @param id the ID of the user to update
     * @param updated the user object containing the updated values
     * @return the updated user if the user exists; otherwise {@code null}
     */
    public User updateUser(int id, User updated) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null){  
            return null;
        }
        existing.setBadgeid(updated.getBadgeid());
        existing.setAddressid(updated.getAddressid());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setEmail(updated.getEmail());
        existing.setFirstName(updated.getFirstName());
        existing.setGender(updated.getGender());
        existing.setLastName(updated.getLastName());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setRatingid(updated.getRatingid());
        existing.setUserType(updated.getUserType());

        return userRepository.save(existing);
    }

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id the ID of the user to delete
     */
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
