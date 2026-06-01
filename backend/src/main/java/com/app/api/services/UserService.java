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
        return userRepository.save(user);
    }

    // Update
    public User updateUser(int id, User updated) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setUserid(updated.getUserid());
        existing.setBadgeid(updated.getBadgeid());

        return userRepository.save(existing);
    }

    // Delete
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}