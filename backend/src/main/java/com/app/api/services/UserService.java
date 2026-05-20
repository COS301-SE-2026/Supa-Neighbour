package com.app.api.services;

import org.springframework.stereotype.Service;
import com.app.api.models.User;
import java.util.List;
@Service
public class UserService 
{

    private List<User> users;

    public UserService() {
        // Initialize the users list with some dummy data
        users = List.of(
            new User(1, "John", "Doe", "password123", "john.doe@example.com", "1234567890", null,"Male", 123, "0", "0", "Regular"),
            new User(2, "Jane", "Smith", "password456", "jane.smith@example.com", "0987654321", null, "Female", 456, "0", "0", "Regular")
        );
    };
    
    /**
     * Test endpoint.
     * @return response User
     */
    public User getUserById(int id) {
        for(User user : users) {
            if(user.getId() == id) {
                return user;
            }
        }
        return null; // Return null if user not found
    }
}
