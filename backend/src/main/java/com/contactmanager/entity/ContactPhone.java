package com.contactmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contact_phones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactPhone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false)
    private String phoneNumber;

    @NotBlank(message = "Label is required")
    @Column(nullable = false)
    private String label; // Work, Home, Personal, Mobile, Other

    public static class PhoneLabel {
        public static final String WORK = "Work";
        public static final String HOME = "Home";
        public static final String PERSONAL = "Personal";
        public static final String MOBILE = "Mobile";
        public static final String OTHER = "Other";
    }
}
