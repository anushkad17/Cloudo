package com.cloudstorage.Cloudo.Repo;

import com.cloudstorage.Cloudo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
}
