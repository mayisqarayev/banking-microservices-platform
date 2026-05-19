package com.mayis.auth_service.service;

import com.mayis.auth_service.dto.CreateUserRoleRequestDto;
import com.mayis.auth_service.exception.UserRoleNotFoundException;
import com.mayis.auth_service.model.entity.UserRole;
import com.mayis.auth_service.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserRoleService {

    private final UserRoleRepository repository;
    private final UserService userService;
    private final RoleService roleService;

    public UserRoleService(UserRoleRepository repository, UserService userService, RoleService roleService) {
        this.repository = repository;
        this.userService = userService;
        this.roleService = roleService;
    }

    public void create(CreateUserRoleRequestDto requestDto) {
        UserRole userRole = new UserRole();
        userRole.setUser(userService.getUserById(requestDto.userId()));
        userRole.setRole(roleService.getRoleById(requestDto.roleId()));

        repository.save(userRole);
    }

    public boolean exists(UUID userId, UUID roleId) {
        return repository.existsByUserIdAndRoleId(userId, roleId);
    }

    public void delete(UUID userId, UUID roleId) {
        UserRole userRole = repository.findByUserIdAndRoleId(userId, roleId)
                .orElseThrow(() -> new UserRoleNotFoundException("User role relation not found"));

        repository.delete(userRole);
    }
}
