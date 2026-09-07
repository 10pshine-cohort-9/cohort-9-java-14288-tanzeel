package com.contactmanager.controller;

import com.contactmanager.dto.ChangePasswordRequest;
import com.contactmanager.dto.UserProfileResponse;
import com.contactmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        log.info("Get profile request received");
        Long userId = (Long) authentication.getPrincipal();
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {
        log.info("Change password request received");
        Long userId = (Long) authentication.getPrincipal();
        userService.changePassword(userId, request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
