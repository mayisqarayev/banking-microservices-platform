package com.mayis.auth_service.service;

import com.mayis.auth_service.dto.GenerateRefreshTokenRequestDto;
import com.mayis.auth_service.model.entity.RefreshToken;
import com.mayis.auth_service.model.entity.User;
import com.mayis.auth_service.repository.RefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RefreshTokenService(RefreshTokenRepository repository, UserService userService, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    public String generateRefreshToken(GenerateRefreshTokenRequestDto requestDto) {
        User user = userService.getUserById(requestDto.userId());
        String rawToken = UUID.randomUUID().toString();
        RefreshToken token = new RefreshToken();

        token.setUser(user);
        token.setTokenHash(passwordEncoder.encode(rawToken));
        token.setExpiresAt(LocalDateTime.now().plusDays(7));
        token.setRevoked(false);

        repository.save(token);
        return rawToken;
    }
}
