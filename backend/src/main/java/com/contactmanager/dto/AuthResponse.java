package com.contactmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String tokenType;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;

    public AuthResponse(String accessToken, Long userId, String email, String firstName, String lastName) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
