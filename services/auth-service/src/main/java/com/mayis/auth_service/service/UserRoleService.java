package com.mayis.auth_service.service;

import com.mayis.auth_service.dto.CreateUserRoleRequestDto;
import com.mayis.auth_service.model.entity.UserRole;
import com.mayis.auth_service.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

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
}