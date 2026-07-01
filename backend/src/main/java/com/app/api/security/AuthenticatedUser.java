package com.app.api.security;

import com.app.api.models.User;

/**
 * Represents an authenticated user within the application.
 * <p>
 * This class acts as a wrapper around the authenticated {@link User}
 * instance and provides access to the user's information during the
 * authentication lifecycle.
 * </p>
 */ 
public class AuthenticatedUser {
    
    /**
     * The authenticated user.
     */
    private final User user;

    /**
     * Creates a new authenticated user wrapper.
     *
     * @param user the authenticated {@link User}; must not be {@code null}
     */
    public AuthenticatedUser(User user) {
        this.user = user;
    }
    
    /**
     * Returns the authenticated user.
     *
     * @return the authenticated {@link User}
     */
    public User getUser() {
        return user;
    }
}
