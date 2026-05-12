package com.mayis.auth_service.service;

import com.mayis.auth_service.config.properties.AuthSecurityProperties;
import com.mayis.auth_service.exception.AccessDeniedException;
import com.mayis.auth_service.dto.ChangePasswordRequestDto;
import com.mayis.auth_service.dto.RegisterRequestDto;
import com.mayis.auth_service.dto.UserResponseDto;
import com.mayis.auth_service.exception.UserAlreadyExistsException;
import com.mayis.auth_service.exception.UserNotFoundException;
import com.mayis.auth_service.model.entity.User;
import com.mayis.auth_service.model.enums.UserStatus;
import com.mayis.auth_service.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthSecurityProperties authSecurityProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authSecurityProperties = authSecurityProperties;
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user;
    }

    protected User getUserByUsername(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return user;
    }

    @Transactional
    public void unlockUser(UUID userId) {
        User user = getUserById(userId);

        user.setFailedLoginAttempts(0);
        user.setAccountNonLocked(true);
    }

    @Transactional
    public void softDeleteUser(UUID userId) {
        User user = getUserById(userId);

        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setEnabled(false);
    }

    @Transactional
    public void restoreUser(UUID userId) {
        User user = getUserById(userId);

        user.setDeleted(false);
        user.setUpdatedAt(LocalDateTime.now());
        user.setEnabled(true);
    }

    @Transactional
    public void changePassword(
            UUID userId,
            ChangePasswordRequestDto request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword()
        )) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(request.newPassword())
        );
    }

    @Transactional
    protected void handleFailedLogin(String username) {
        userRepository.findByUsernameAndDeletedFalse(username)
                .ifPresent(user -> {
                    int attempts = user.getFailedLoginAttempts() + 1;
                    user.setFailedLoginAttempts(attempts);

                    if (attempts >= authSecurityProperties.maxFailedLoginAttempts()) {
                        user.setAccountNonLocked(false);
                    }
                });
    }

    @Transactional
    protected void handleSuccessfulLogin(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
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

    public List<UserResponseDto> getAll() {
        return userRepository.findAllByDeletedFalse()
                .stream()
                .map(i -> {
                    return new UserResponseDto(
                            i.getId(),
                            i.getUsername(),
                            i.getEmail(),
                            i.getFirstName(),
                            i.getLastName(),
                            i.getStatus(),
                            i.getRoles().stream()
                                    .map(userRole -> userRole.getRole().getAuthority())
                                    .collect(Collectors.toSet())
                    );
                }).toList();
    }
}
