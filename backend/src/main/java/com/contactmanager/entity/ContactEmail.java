package com.contactmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contact_emails")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Label is required")
    @Column(nullable = false)
    private String label; // Work, Personal, Other

    public static class EmailLabel {
        public static final String WORK = "Work";
        public static final String PERSONAL = "Personal";
        public static final String OTHER = "Other";
    }
}
