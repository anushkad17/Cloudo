package com.cloudstorage.Cloudo.service;

import com.cloudstorage.Cloudo.models.User;
import com.cloudstorage.Cloudo.Repo.UserRepo;
import com.cloudstorage.Cloudo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public String login(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Assuming your User entity has getRole()
        return jwtService.generateToken(user.getUsername(), user.getRole());
    }

    public String register(String username, String password, String role) {
        if (userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        var user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepo.save(user);

        return jwtService.generateToken(user.getUsername(), user.getRole());
    }

}
