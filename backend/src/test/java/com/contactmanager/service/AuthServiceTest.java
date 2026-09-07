package com.contactmanager.service;

import com.contactmanager.dto.AuthResponse;
import com.contactmanager.dto.LoginRequest;
import com.contactmanager.dto.RegisterRequest;
import com.contactmanager.entity.User;
import com.contactmanager.exception.DuplicateResourceException;
import com.contactmanager.exception.UnauthorizedException;
import com.contactmanager.repository.UserRepository;
import com.contactmanager.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .password("Password123!")
                .confirmPassword("Password123!")
                .build();

        loginRequest = LoginRequest.builder()
                .email("john@example.com")
                .password("Password123!")
                .build();

        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .password("encodedPassword")
                .build();
    }

    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateTokenFromUserId(1L)).thenReturn("token123");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("token123", response.getAccessToken());
        assertEquals("john@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterDuplicatePhone() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterPasswordMismatch() {
        registerRequest.setConfirmPassword("DifferentPassword123!");

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginWithEmailSuccess() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateTokenFromUserId(1L)).thenReturn("token123");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("token123", response.getAccessToken());
        assertEquals("john@example.com", response.getEmail());
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
    }

    @Test
    void testLoginWithPhoneSuccess() {
        LoginRequest phoneLogin = LoginRequest.builder()
                .phoneNumber("1234567890")
                .password("Password123!")
                .build();

        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(phoneLogin.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateTokenFromUserId(1L)).thenReturn("token123");

        AuthResponse response = authService.login(phoneLogin);

        assertNotNull(response);
        assertEquals("token123", response.getAccessToken());
        verify(userRepository, times(1)).findByPhoneNumber("1234567890");
    }

    @Test
    void testLoginInvalidPassword() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
        verify(jwtTokenProvider, never()).generateTokenFromUserId(anyLong());
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLoginNoCredentials() {
        LoginRequest invalidLogin = LoginRequest.builder()
                .password("Password123!")
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.login(invalidLogin));
    }
}
