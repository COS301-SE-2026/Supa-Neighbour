package com.app.api.dtos;

/**
 * Represents the request for a user login.
 */
public class LoginRequest {
    
    /**
     * The email of the user.
     */
    private String email;
    
    /**
     * The password of the user.
     */
    private String password;

    /**
     * Default no-args constructor required by JPA.
     */
    public LoginRequest() {
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

    /**
     * Returns the user's password.
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password the user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
