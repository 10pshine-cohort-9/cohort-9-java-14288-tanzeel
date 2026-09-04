package com.contactmanager.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Title is required")
    private String title;

    private String company;

    private String notes;

    @Valid
    private List<ContactEmailDto> emails = new ArrayList<>();

    @Valid
    private List<ContactPhoneDto> phones = new ArrayList<>();
}
