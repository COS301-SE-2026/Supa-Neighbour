package com.app.api.dtos;

/**
 * Represents the response for a user login request.
 */

public class LoginResponse {
    /**
     * The authentication token for the user.
     */
    private String token;
    
    /**
     * The email of the user.
     */
    private String email;

    /**
     * Default no-args constructor required by JPA.
     */
    public LoginResponse() {
    }

    /**
     * Returns the authentication token.
     *
     * @return the authentication token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the authentication token.
     *
     * @param token the authentication token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Returns the user's email.
     *
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
