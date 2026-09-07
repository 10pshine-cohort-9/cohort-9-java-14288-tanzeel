package com.contactmanager.service;

import com.contactmanager.dto.ContactEmailDto;
import com.contactmanager.dto.ContactPhoneDto;
import com.contactmanager.dto.ContactRequest;
import com.contactmanager.dto.ContactResponse;
import com.contactmanager.entity.Contact;
import com.contactmanager.entity.ContactEmail;
import com.contactmanager.entity.ContactPhone;
import com.contactmanager.entity.User;
import com.contactmanager.exception.ResourceNotFoundException;
import com.contactmanager.exception.UnauthorizedException;
import com.contactmanager.repository.ContactRepository;
import com.contactmanager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Slf4j
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<ContactResponse> getContacts(Long userId, Pageable pageable) {
        log.debug("Fetching contacts for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Contact> contacts = contactRepository.findByUser(user, pageable);
        return contacts.map(this::convertToResponse);
    }

    public Page<ContactResponse> searchContacts(Long userId, String search, Pageable pageable) {
        log.debug("Searching contacts for user: {} with search term: {}", userId, search);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Contact> contacts = contactRepository.searchContacts(user, search, pageable);
        return contacts.map(this::convertToResponse);
    }

    public ContactResponse getContact(Long userId, Long contactId) {
        log.debug("Fetching contact {} for user: {}", contactId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact contact = contactRepository.findByIdAndUser(contactId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        return convertToResponse(contact);
    }

    @Transactional
    public ContactResponse createContact(Long userId, ContactRequest request) {
        log.info("Creating contact for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact contact = Contact.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .title(request.getTitle())
                .company(request.getCompany())
                .notes(request.getNotes())
                .build();

        // Add emails
        if (request.getEmails() != null) {
            contact.setEmails(request.getEmails().stream()
                    .map(emailDto -> ContactEmail.builder()
                            .contact(contact)
                            .email(emailDto.getEmail())
                            .label(emailDto.getLabel())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Add phones
        if (request.getPhones() != null) {
            contact.setPhones(request.getPhones().stream()
                    .map(phoneDto -> ContactPhone.builder()
                            .contact(contact)
                            .phoneNumber(phoneDto.getPhoneNumber())
                            .label(phoneDto.getLabel())
                            .build())
                    .collect(Collectors.toList()));
        }

        Contact savedContact = contactRepository.save(contact);
        log.info("Contact created successfully with ID: {} for user: {}", savedContact.getId(), userId);

        return convertToResponse(savedContact);
    }

    @Transactional
    public ContactResponse updateContact(Long userId, Long contactId, ContactRequest request) {
        log.info("Updating contact {} for user: {}", contactId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact contact = contactRepository.findByIdAndUser(contactId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setTitle(request.getTitle());
        contact.setCompany(request.getCompany());
        contact.setNotes(request.getNotes());

        // Update emails
        contact.getEmails().clear();
        if (request.getEmails() != null) {
            contact.getEmails().addAll(request.getEmails().stream()
                    .map(emailDto -> ContactEmail.builder()
                            .contact(contact)
                            .email(emailDto.getEmail())
                            .label(emailDto.getLabel())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Update phones
        contact.getPhones().clear();
        if (request.getPhones() != null) {
            contact.getPhones().addAll(request.getPhones().stream()
                    .map(phoneDto -> ContactPhone.builder()
                            .contact(contact)
                            .phoneNumber(phoneDto.getPhoneNumber())
                            .label(phoneDto.getLabel())
                            .build())
                    .collect(Collectors.toList()));
        }

        Contact updatedContact = contactRepository.save(contact);
        log.info("Contact updated successfully with ID: {} for user: {}", contactId, userId);

        return convertToResponse(updatedContact);
    }

    @Transactional
    public void deleteContact(Long userId, Long contactId) {
        log.info("Deleting contact {} for user: {}", contactId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Contact contact = contactRepository.findByIdAndUser(contactId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        contactRepository.delete(contact);
        log.info("Contact deleted successfully with ID: {} for user: {}", contactId, userId);
    }

    private ContactResponse convertToResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .title(contact.getTitle())
                .company(contact.getCompany())
                .notes(contact.getNotes())
                .emails(contact.getEmails().stream()
                        .map(email -> ContactEmailDto.builder()
                                .id(email.getId())
                                .email(email.getEmail())
                                .label(email.getLabel())
                                .build())
                        .collect(Collectors.toList()))
                .phones(contact.getPhones().stream()
                        .map(phone -> ContactPhoneDto.builder()
                                .id(phone.getId())
                                .phoneNumber(phone.getPhoneNumber())
                                .label(phone.getLabel())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(contact.getCreatedAt())
                .updatedAt(contact.getUpdatedAt())
                .build();
    }
}
