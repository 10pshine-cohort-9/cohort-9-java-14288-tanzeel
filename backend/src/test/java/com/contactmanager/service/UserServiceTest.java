package com.contactmanager.service;

import com.contactmanager.dto.ChangePasswordRequest;
import com.contactmanager.dto.UserProfileResponse;
import com.contactmanager.entity.User;
import com.contactmanager.exception.ResourceNotFoundException;
import com.contactmanager.exception.UnauthorizedException;
import com.contactmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private ChangePasswordRequest changePasswordRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .password("encodedOldPassword")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        changePasswordRequest = ChangePasswordRequest.builder()
                .currentPassword("OldPassword123!")
                .newPassword("NewPassword123!")
                .confirmPassword("NewPassword123!")
                .build();
    }

    @Test
    void testGetUserProfileSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserProfileResponse response = userService.getUserProfile(1L);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("1234567890", response.getPhoneNumber());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserProfileNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> userService.getUserProfile(1L));
    }

    @Test
    void testGetUserProfileFullName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserProfileResponse response = userService.getUserProfile(1L);

        assertEquals("John Doe", response.getFullName());
    }

    @Test
    void testChangePasswordSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("OldPassword123!", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPassword123!")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changePassword(1L, changePasswordRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testChangePasswordUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> userService.changePassword(1L, changePasswordRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePasswordInvalidCurrentPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("OldPassword123!", testUser.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, 
                () -> userService.changePassword(1L, changePasswordRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePasswordMismatch() {
        changePasswordRequest.setConfirmPassword("DifferentPassword123!");

        assertThrows(IllegalArgumentException.class, 
                () -> userService.changePassword(1L, changePasswordRequest));
    }

    @Test
    void testChangePasswordSameAsOld() {
        ChangePasswordRequest samePasswordRequest = ChangePasswordRequest.builder()
                .currentPassword("OldPassword123!")
                .newPassword("OldPassword123!")
                .confirmPassword("OldPassword123!")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, 
                () -> userService.changePassword(1L, samePasswordRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePasswordEmptyFields() {
        ChangePasswordRequest invalidRequest = ChangePasswordRequest.builder()
                .currentPassword("")
                .newPassword("")
                .confirmPassword("")
                .build();

        assertThrows(IllegalArgumentException.class, 
                () -> userService.changePassword(1L, invalidRequest));
    }
}
