package com.mayis.auth_service.repository;

import com.mayis.auth_service.model.entity.Role;
import com.mayis.auth_service.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);

    List<Role> findAllByDeletedFalse();
}
