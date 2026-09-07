package com.contactmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    private String email;
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;

    // Either email or phoneNumber must be provided
}
