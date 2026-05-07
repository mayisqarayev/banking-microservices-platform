package com.mayis.auth_service.service;

import com.mayis.auth_service.dto.RegisterRequestDto;
import com.mayis.auth_service.dto.UserResponseDto;
import com.mayis.auth_service.exception.UserAlreadyExistsException;
import com.mayis.auth_service.exception.UserNotFoundException;
import com.mayis.auth_service.model.entity.User;
import com.mayis.auth_service.model.enums.UserStatus;
import com.mayis.auth_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
                            i.getStatus()
                    );
                }).toList();
    }
}
