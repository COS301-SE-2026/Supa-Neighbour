package com.app.api.dtos;

import java.sql.Date;
import lombok.Data;

@Data
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String password;
    private String phoneNumber;
    private Date dateOfBirth;
    private String gender;
    private String userType;
    private String username;
    private Integer addressId;
    private Integer badgeId;
    private Integer ratingId;
}

