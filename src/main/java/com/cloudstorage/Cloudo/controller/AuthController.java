package com.cloudstorage.Cloudo.controller;

import com.cloudstorage.Cloudo.DTO.AuthRequest;
import com.cloudstorage.Cloudo.DTO.AuthResponse;
import com.cloudstorage.Cloudo.DTO.RegisterRequest;
import com.cloudstorage.Cloudo.DTO.UserProfileDto;
import com.cloudstorage.Cloudo.Repo.UserRepo;
import com.cloudstorage.Cloudo.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import com.cloudstorage.Cloudo.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String role = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        String token = jwtService.generateToken(request.getUsername(), role);
        return new AuthResponse(token);
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepo.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        if (userRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getName())
                .role("ROLE_USER")
                .build();

        userRepo.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User user = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // You can make a DTO to avoid exposing password
        return ResponseEntity.ok(new UserProfileDto(
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        ));
    }


}
