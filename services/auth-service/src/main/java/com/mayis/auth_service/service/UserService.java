package com.mayis.auth_service.service;

import com.mayis.auth_service.config.properties.AuthSecurityProperties;
import com.mayis.auth_service.dto.RegisterRequestDto;
import com.mayis.auth_service.dto.UserResponseDto;
import com.mayis.auth_service.exception.UserAlreadyExistsException;
import com.mayis.auth_service.exception.UserNotFoundException;
import com.mayis.auth_service.model.entity.User;
import com.mayis.auth_service.model.enums.UserStatus;
import com.mayis.auth_service.repository.UserRepository;
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
    protected void handleFailedLogin(String username) {
        User user = getUserByUsername(username);

        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= authSecurityProperties.maxFailedLoginAttempts()) {
            user.setAccountNonLocked(false);
        }
    }

    @Transactional
    protected void handleSuccessfulLogin(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
    }

    public UserResponseDto getCurrentUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus(),
                user.getRoles().stream().map(String::valueOf).collect(Collectors.toSet())
        );
    }

    public List<UserResponseDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(i -> {
                    return new UserResponseDto(
                            i.getId(),
                            i.getUsername(),
                            i.getEmail(),
                            i.getFirstName(),
                            i.getLastName(),
                            i.getStatus(),
                            i.getRoles().stream().map(String::valueOf).collect(Collectors.toSet())
                    );
                }).toList();
    }
}
