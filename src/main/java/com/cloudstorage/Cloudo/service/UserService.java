package com.cloudstorage.Cloudo.service;

import com.cloudstorage.Cloudo.models.User;
import com.cloudstorage.Cloudo.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    /**
     * Get user by ID
     */
    public User getById(UUID id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    /**
     * Get user by username
     */
    public User getByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

    /**
     * Get currently authenticated user from Security Context
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        String username = authentication.getName();
        return getByUsername(username);
    }
}
