package com.mayis.auth_service.service;

import com.mayis.auth_service.config.properties.AuthSecurityProperties;
import com.mayis.auth_service.dto.AssignRoleRequestDto;
import com.mayis.auth_service.dto.ChangePasswordRequestDto;
import com.mayis.auth_service.dto.CreateUserRoleRequestDto;
import com.mayis.auth_service.dto.RegisterRequestDto;
import com.mayis.auth_service.dto.UserResponseDto;
import com.mayis.auth_service.exception.*;
import com.mayis.auth_service.model.entity.Role;
import com.mayis.auth_service.model.entity.User;
import com.mayis.auth_service.model.enums.RoleName;
import com.mayis.auth_service.model.enums.UserStatus;
import com.mayis.auth_service.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthSecurityProperties authSecurityProperties;
    private final RoleService roleService;
    private final UserRoleService userRoleService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthSecurityProperties authSecurityProperties,
            RoleService roleService,
            @Lazy UserRoleService userRoleService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authSecurityProperties = authSecurityProperties;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
    }

    protected User createUser(RegisterRequestDto requestDto) {
        if (userRepository.existsByUsername(requestDto.username())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(requestDto.email())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setUsername(requestDto.username());
        user.setEmail(requestDto.email());
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setFailedLoginAttempts(0);

        return userRepository.save(user);
    }

    protected User getUserById(UUID id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user;
    }

    protected User getUserByUsername(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return user;
    }

    @Transactional
    public void activateUser(UUID userID) {
        User user = getUserById(userID);

        if (user.getStatus() == UserStatus.ACTIVE && user.isEnabled()) {
            throw new UserAlreadyActiveException("User is already active");
        } else if (user.getStatus() == UserStatus.SUSPENDED || user.getStatus() == UserStatus.DELETED) {
            throw new InvalidUserStateException("User is not in a valid state for activate");
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);

        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(UUID userID) {
        User user = getUserById(userID);

        if (user.getStatus() == UserStatus.INACTIVE && !user.isEnabled()) {
            throw new UserAlreadyInactiveException("User is already inactive");
        } else if (user.getStatus() == UserStatus.SUSPENDED || user.getStatus() == UserStatus.DELETED) {
            throw new InvalidUserStateException("User is not in a valid state for deactivate");
        }

        user.setStatus(UserStatus.INACTIVE);
        user.setEnabled(false);

        userRepository.save(user);
    }

    @Transactional
    public void suspendUser(UUID userId, String actorUsername) {
        User actor = getUserByUsername(actorUsername);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getId().equals(actor.getId())) {
            throw new SameUserOperationException("Admin cannot suspend himself");
        }

        if (user.isDeleted() || user.getStatus() == UserStatus.DELETED) {
            throw new InvalidUserStateException("User is not in a valid state for suspend");
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new UserAlreadySuspendedException("User is already suspended");
        }

        user.setStatus(UserStatus.SUSPENDED);
        user.setAccountNonLocked(false);

        userRepository.save(user);
    }

    @Transactional
    public void unsuspendUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isDeleted() || user.getStatus() == UserStatus.DELETED) {
            throw new InvalidUserStateException("User is not in a valid state for unsuspend");
        }

        if (user.getStatus() != UserStatus.SUSPENDED) {
            throw new UserNotSuspendedException("User is not suspended");
        }

        user.setFailedLoginAttempts(0);
        user.setAccountNonLocked(true);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);
    }

    @Transactional
    public void softDeleteUser(UUID userId, String actorUsername) {
        User actor = getUserByUsername(actorUsername);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isDeleted()) {
            throw new UserAlreadyDeletedException("User is already deleted");
        }

        if (user.getId().equals(actor.getId())) {
            throw new SameUserOperationException("Admin cannot delete himself");
        }

        user.setDeleted(true);
        user.setStatus(UserStatus.DELETED);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(actor.getId());
        user.setFailedLoginAttempts(0);
        user.setEnabled(false);
        user.setAccountNonLocked(false);
        user.setAccountNonExpired(false);
        user.setCredentialsNonExpired(false);

        userRepository.save(user);
    }

    @Transactional
    public void restoreUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isDeleted()) {
            throw new UserAlreadyRestoredException("User is already restored");
        }

        user.setDeleted(false);
        user.setDeletedAt(null);
        user.setDeletedBy(null);
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setFailedLoginAttempts(0);

        userRepository.save(user);
    }

    @Transactional
    public void changePassword(
            UUID userId,
            ChangePasswordRequestDto request,
            String currentUsername
    ) {
        User currentUser = getUserByUsername(currentUsername);

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to change this user's password");
        }

        User user = getUserById(userId);

        if (user.getStatus() != UserStatus.ACTIVE
                || !user.isEnabled()
                || !user.isAccountNonLocked()
                || !user.isAccountNonExpired()
                || !user.isCredentialsNonExpired()) {
            throw new InvalidUserStateException("User is not in a valid state for password change");
        }

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword()
        )) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(request.newPassword())
        );

        userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void handleFailedLogin(String username) {
        userRepository.findByUsernameAndDeletedFalse(username)
                .ifPresent(user -> {
                    int attempts = user.getFailedLoginAttempts() + 1;
                    user.setFailedLoginAttempts(attempts);

                    if (attempts >= authSecurityProperties.maxFailedLoginAttempts()) {
                        user.setStatus(UserStatus.SUSPENDED);
                        user.setAccountNonLocked(false);
                    }

                    userRepository.save(user);
                });
    }

    @Transactional
    protected void handleSuccessfulLogin(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());

        userRepository.save(user);
    }

    public UserResponseDto getCurrentAuthenticatedUser(String currentUsername) {
        User user = getUserByUsername(currentUsername);
        return mapToUserResponse(user);
    }

    @Transactional
    public void assignRole(UUID userId, AssignRoleRequestDto request) {
        User user = getUserById(userId);
        Role role = roleService.getRoleByName(request.role());

        if (userRoleService.exists(user.getId(), role.getId())) {
            throw new UserRoleAlreadyExistsException("User already has this role");
        }

        userRoleService.create(new CreateUserRoleRequestDto(user.getId(), role.getId()));
    }

    @Transactional
    public void removeRole(UUID userId, RoleName roleName) {
        User user = getUserById(userId);
        Role role = roleService.getRoleByName(roleName);

        userRoleService.delete(user.getId(), role.getId());
    }

    public UserResponseDto getCurrentUser(UUID id, String currentUsername) {
        User currentUser = getUserByUsername(currentUsername);
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));

        if (!isAdmin && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You are not allowed to access this user");
        }

        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return mapToUserResponse(user);
    }

    public List<UserResponseDto> getAll() {
        return userRepository.findAllByDeletedFalse()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    private UserResponseDto mapToUserResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus(),
                user.getRoles().stream()
                        .map(userRole -> userRole.getRole().getAuthority())
                        .collect(Collectors.toSet())
        );
    }
}
