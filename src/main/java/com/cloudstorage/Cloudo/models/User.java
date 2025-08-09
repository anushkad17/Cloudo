package com.cloudstorage.Cloudo.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_email_unique", columnList = "email", unique = true)
})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String role;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // ===== Overrides for Spring Security =====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // If you're not using roles yet, return an empty list
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // set to false if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // set to false if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // set to false if needed
    }

    @Override
    public boolean isEnabled() {
        return true; // set to false if user is deactivated
    }


}
