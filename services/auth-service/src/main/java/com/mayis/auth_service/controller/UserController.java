package com.mayis.auth_service.controller;

import com.mayis.auth_service.dto.AssignRoleRequestDto;
import com.mayis.auth_service.dto.ChangePasswordRequestDto;
import com.mayis.auth_service.dto.UserResponseDto;
import com.mayis.auth_service.model.enums.RoleName;
import com.mayis.auth_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponseDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/me")
    public UserResponseDto getCurrentAuthenticatedUser(Authentication authentication) {
        return userService.getCurrentAuthenticatedUser(authentication.getName());
    }

    @GetMapping("/{id}")
    public UserResponseDto getCurrentUserById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return userService.getCurrentUser(id, authentication.getName());
    }

    @PatchMapping("/{userId}/change-password")
    public void changePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangePasswordRequestDto request,
            Authentication authentication
    ) {
        userService.changePassword(userId, request, authentication.getName());
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        userService.softDeleteUser(userId, authentication.getName());
    }

    @PatchMapping("/{userId}/restore")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void restoreUser(
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        userService.restoreUser(userId, authentication.getName());
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void assignRole(
            @PathVariable UUID userId,
            @Valid @RequestBody AssignRoleRequestDto request,
            Authentication authentication
    ) {
        userService.assignRole(userId, request, authentication.getName());
    }

    @DeleteMapping("/{userId}/roles/{role}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void removeRole(
            @PathVariable UUID userId,
            @PathVariable RoleName role,
            Authentication authentication
    ) {
        userService.removeRole(userId, role, authentication.getName());
    }

    @PatchMapping("/{userId}/deactivate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deactivateUser(
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        userService.deactivateUser(userId, authentication.getName());
    }

    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void activateUser(
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        userService.activateUser(userId, authentication.getName());
    }

    @PatchMapping("/{userId}/suspend")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void suspendUser(
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        userService.suspendUser(userId, authentication.getName());
    }

    @PatchMapping("/{userId}/unsuspend")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void unsuspendUser(
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        userService.unsuspendUser(userId, authentication.getName());
    }
}
