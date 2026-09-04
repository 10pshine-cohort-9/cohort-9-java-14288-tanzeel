package com.contactmanager.controller;

import com.contactmanager.dto.ContactRequest;
import com.contactmanager.dto.ContactResponse;
import com.contactmanager.service.ContactService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin
@Slf4j
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping
    public ResponseEntity<Page<ContactResponse>> getContacts(
            Authentication authentication,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        log.info("Get contacts request received with search: {}", search);
        Long userId = (Long) authentication.getPrincipal();
        
        Page<ContactResponse> contacts;
        if (search != null && !search.isEmpty()) {
            contacts = contactService.searchContacts(userId, search, pageable);
        } else {
            contacts = contactService.getContacts(userId, pageable);
        }
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getContact(
            Authentication authentication,
            @PathVariable Long id) {
        log.info("Get contact {} request received", id);
        Long userId = (Long) authentication.getPrincipal();
        ContactResponse contact = contactService.getContact(userId, id);
        return ResponseEntity.ok(contact);
    }

    @PostMapping
    public ResponseEntity<ContactResponse> createContact(
            Authentication authentication,
            @Valid @RequestBody ContactRequest request) {
        log.info("Create contact request received");
        Long userId = (Long) authentication.getPrincipal();
        ContactResponse contact = contactService.createContact(userId, request);
        return new ResponseEntity<>(contact, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponse> updateContact(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ContactRequest request) {
        log.info("Update contact {} request received", id);
        Long userId = (Long) authentication.getPrincipal();
        ContactResponse contact = contactService.updateContact(userId, id, request);
        return ResponseEntity.ok(contact);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(
            Authentication authentication,
            @PathVariable Long id) {
        log.info("Delete contact {} request received", id);
        Long userId = (Long) authentication.getPrincipal();
        contactService.deleteContact(userId, id);
        return ResponseEntity.noContent().build();
    }
}
