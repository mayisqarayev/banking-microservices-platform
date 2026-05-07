package com.mayis.auth_service.repository;

import com.mayis.auth_service.model.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"roles", "roles.role"})
    Optional<User> findByUsernameAndDeletedFalse(String username);

    @EntityGraph(attributePaths = {"roles", "roles.role"})
    Optional<User> findByEmailAndDeletedFalse(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
