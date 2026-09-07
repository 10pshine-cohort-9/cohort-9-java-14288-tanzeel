package com.contactmanager.service;

import com.contactmanager.dto.AuthResponse;
import com.contactmanager.dto.ChangePasswordRequest;
import com.contactmanager.dto.LoginRequest;
import com.contactmanager.dto.RegisterRequest;
import com.contactmanager.dto.UserProfileResponse;
import com.contactmanager.entity.User;
import com.contactmanager.exception.DuplicateResourceException;
import com.contactmanager.exception.ResourceNotFoundException;
import com.contactmanager.exception.UnauthorizedException;
import com.contactmanager.repository.UserRepository;
import com.contactmanager.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Validate request
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (request.getEmail() == null && request.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Either email or phone number must be provided");
        }

        // Check for duplicate email
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new DuplicateResourceException("Email is already registered");
        }

        // Check for duplicate phone
        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            log.warn("Phone number already exists: {}", request.getPhoneNumber());
            throw new DuplicateResourceException("Phone number is already registered");
        }

        // Create and save user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Generate token
        String token = jwtTokenProvider.generateTokenFromUserId(savedUser.getId());

        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(),
                savedUser.getFirstName(), savedUser.getLastName());
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt with email/phone");

        if ((request.getEmail() == null || request.getEmail().isEmpty()) &&
                (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty())) {
            throw new IllegalArgumentException("Either email or phone number must be provided");
        }

        User user = null;

        // Try email first
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isPresent()) {
                user = userOpt.get();
            }
        }

        // Try phone if email didn't work
        if (user == null && request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            Optional<User> userOpt = userRepository.findByPhoneNumber(request.getPhoneNumber());
            if (userOpt.isPresent()) {
                user = userOpt.get();
            }
        }

        if (user == null) {
            log.warn("Login failed: User not found");
            throw new UnauthorizedException("Invalid credentials");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid password for user: {}", user.getId());
            throw new UnauthorizedException("Invalid credentials");
        }

        log.info("User logged in successfully: {}", user.getId());

        // Generate token
        String token = jwtTokenProvider.generateTokenFromUserId(user.getId());

        return new AuthResponse(token, user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
    }
}
