package com.contactmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private String company;
    private String notes;
    private List<ContactEmailDto> emails;
    private List<ContactPhoneDto> phones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPrimaryEmail() {
        if (emails != null && !emails.isEmpty()) {
            return emails.get(0).getEmail();
        }
        return null;
    }

    public String getPrimaryPhone() {
        if (phones != null && !phones.isEmpty()) {
            return phones.get(0).getPhoneNumber();
        }
        return null;
    }
}
