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
public class ContactPhoneDto {

    private Long id;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Label is required")
    private String label; // Work, Home, Personal, Mobile, Other
}
