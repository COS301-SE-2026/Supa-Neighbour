package com.app.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserSummaryDTO {
    private int userId;
    private String username;
    private String firstName;
    private String lastName;
}
