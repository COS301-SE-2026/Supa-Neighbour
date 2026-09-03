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

    /**
     * Constructs the service with its required repository dependency.
     *
     * @param userRepository repository providing analytics data for user
     */
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
        if (updated.getFirebaseUid() != null) {
            existing.setFirebaseUid(updated.getFirebaseUid());
        }
        if (updated.getUsername() != null) {
            existing.setUsername(updated.getUsername());
        }
        if (updated.getFirstName() != null){
            existing.setFirstName(updated.getFirstName());
        }
        if (updated.getLastName() != null) {
            existing.setLastName(updated.getLastName());
        }
        if (updated.getEmail() != null){ 
            existing.setEmail(updated.getEmail());
        }
        if (updated.getPhoneNumber() != null){ 
            existing.setPhoneNumber(updated.getPhoneNumber());
        }
        if (updated.getDateOfBirth() != null){ 
            existing.setDateOfBirth(updated.getDateOfBirth());
        }
        if (updated.getGender() != null){
             existing.setGender(updated.getGender());
        }
        if (updated.getIsAdmin() != null){ 
            existing.setIsAdmin(updated.getIsAdmin());
        }
        if (updated.getUserType() != null) {
            existing.setUserType(updated.getUserType());
        }

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
