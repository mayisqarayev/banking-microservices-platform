package com.mayis.auth_service.service;

import com.mayis.auth_service.exception.RoleNotFoundException;
import com.mayis.auth_service.model.entity.Role;
import com.mayis.auth_service.model.enums.RoleName;
import com.mayis.auth_service.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    protected Role getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
    }

    protected Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
    }
}
