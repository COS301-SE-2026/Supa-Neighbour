package com.app.api.security;

import com.app.api.models.User;

public class AuthenticatedUser {
    
    private final User user;

    public AuthenticatedUser(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}
