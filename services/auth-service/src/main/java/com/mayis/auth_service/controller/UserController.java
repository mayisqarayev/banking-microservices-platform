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

    @PatchMapping("/{userId}/unlock")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void unlockUser(@PathVariable UUID userId) {
        userService.unlockUser(userId);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(@PathVariable UUID userId) {
        userService.softDeleteUser(userId);
    }

    @PatchMapping("/{userId}/restore")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void restoreUser(@PathVariable UUID userId) {
        userService.restoreUser(userId);
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void assignRole(
            @PathVariable UUID userId,
            @Valid @RequestBody AssignRoleRequestDto request
    ) {
        userService.assignRole(userId, request);
    }

    @DeleteMapping("/{userId}/roles/{role}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void removeRole(
            @PathVariable UUID userId,
            @PathVariable RoleName role
    ) {
        userService.removeRole(userId, role);
    }
}
