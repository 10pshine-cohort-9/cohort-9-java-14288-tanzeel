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
import com.contactmanager.repository.ContactRepository;
import com.contactmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContactService contactService;

    private User testUser;
    private Contact testContact;
    private ContactRequest contactRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        testContact = Contact.builder()
                .id(1L)
                .user(testUser)
                .firstName("Jane")
                .lastName("Smith")
                .title("Manager")
                .company("Tech Company")
                .notes("Important contact")
                .emails(new ArrayList<>(Arrays.asList(
                        ContactEmail.builder()
                                .id(1L)
                                .email("jane@example.com")
                                .label("Work")
                                .build()
                )))
                .phones(new ArrayList<>(Arrays.asList(
                        ContactPhone.builder()
                                .id(1L)
                                .phoneNumber("9876543210")
                                .label("Mobile")
                                .build()
                )))
                .build();

        contactRequest = ContactRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .title("Manager")
                .company("Tech Company")
                .notes("Important contact")
                .emails(Arrays.asList(
                        ContactEmailDto.builder()
                                .email("jane@example.com")
                                .label("Work")
                                .build()
                ))
                .phones(Arrays.asList(
                        ContactPhoneDto.builder()
                                .phoneNumber("9876543210")
                                .label("Mobile")
                                .build()
                ))
                .build();
    }

    @Test
    void testGetContactsSuccess() {
        Page<Contact> contactPage = new PageImpl<>(Arrays.asList(testContact));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contactRepository.findByUser(testUser, PageRequest.of(0, 10)))
                .thenReturn(contactPage);

        Page<ContactResponse> response = contactService.getContacts(1L, PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Jane", response.getContent().get(0).getFirstName());
        verify(contactRepository, times(1)).findByUser(testUser, PageRequest.of(0, 10));
    }

    @Test
    void testGetContactsUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> contactService.getContacts(1L, PageRequest.of(0, 10)));
    }

    @Test
    void testGetContactSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contactRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testContact));

        ContactResponse response = contactService.getContact(1L, 1L);

        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        assertEquals("jane@example.com", response.getPrimaryEmail());
        verify(contactRepository, times(1)).findByIdAndUser(1L, testUser);
    }

    @Test
    void testGetContactNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contactRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> contactService.getContact(1L, 1L));
    }

    @Test
    void testCreateContactSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);

        ContactResponse response = contactService.createContact(1L, contactRequest);

        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        assertEquals(1, response.getEmails().size());
        assertEquals(1, response.getPhones().size());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void testCreateContactUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> contactService.createContact(1L, contactRequest));
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void testUpdateContactSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contactRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);

        ContactResponse response = contactService.updateContact(1L, 1L, contactRequest);

        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void testUpdateContactNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contactRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> contactService.updateContact(1L, 1L, contactRequest));
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void testDeleteContactSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contactRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testContact));

        contactService.deleteContact(1L, 1L);

        verify(contactRepository, times(1)).delete(testContact);
    }

    @Test
    void testDeleteContactNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contactRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> contactService.deleteContact(1L, 1L));
        verify(contactRepository, never()).delete(any(Contact.class));
    }

    @Test
    void testSearchContactsSuccess() {
        Page<Contact> contactPage = new PageImpl<>(Arrays.asList(testContact));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contactRepository.searchContacts(testUser, "Jane", PageRequest.of(0, 10)))
                .thenReturn(contactPage);

        Page<ContactResponse> response = contactService.searchContacts(1L, "Jane", PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(contactRepository, times(1)).searchContacts(testUser, "Jane", PageRequest.of(0, 10));
    }

    @Test
    void testUserCanOnlyAccessOwnContact() {
        User anotherUser = User.builder().id(2L).build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        when(contactRepository.findByIdAndUser(1L, anotherUser)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> contactService.getContact(2L, 1L));
    }
}
