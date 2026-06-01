package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.User;
import com.app.api.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Get all
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get by id
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    // Create
    public User saveUser(User user) {
        if(user == null) return null;
        return userRepository.save(user);
    }

    // Update
    public User updateUser(int id, User updated) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null) return null;

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
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}