package com.contactmanager.repository;

import com.contactmanager.entity.Contact;
import com.contactmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    Page<Contact> findByUser(User user, Pageable pageable);

    Optional<Contact> findByIdAndUser(Long id, User user);

    @Query("SELECT c FROM Contact c WHERE c.user = :user AND " +
           "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.company) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Contact> searchContacts(@Param("user") User user, @Param("search") String search, Pageable pageable);
}
