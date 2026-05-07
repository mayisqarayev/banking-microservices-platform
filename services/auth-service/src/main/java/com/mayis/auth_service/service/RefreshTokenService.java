package com.mayis.auth_service.service;

import com.mayis.auth_service.dto.GenerateRefreshTokenRequestDto;
import com.mayis.auth_service.model.entity.RefreshToken;
import com.mayis.auth_service.model.entity.User;
import com.mayis.auth_service.repository.RefreshTokenRepository;
import com.mayis.auth_service.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;



@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UserService userService;
    private final TokenHashService tokenHashService;
    private final JwtService jwtService;

    public RefreshTokenService(RefreshTokenRepository repository, UserService userService, TokenHashService tokenHashService, JwtService jwtService) {
        this.repository = repository;
        this.userService = userService;
        this.tokenHashService = tokenHashService;
        this.jwtService = jwtService;
    }


    protected String generateRefreshToken(GenerateRefreshTokenRequestDto requestDto) {
        User user = userService.getUserById(requestDto.userId());
        String rawRefreshToken = jwtService.generateRefreshToken(user);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(tokenHashService.hash(rawRefreshToken));
        token.setExpiresAt(LocalDateTime.now().plusDays(7));
        token.setRevoked(false);

        repository.save(token);
        return rawRefreshToken;
    }

    protected RefreshToken findValidRefreshToken(User user, String rawRefreshToken) {
        String hashedToken = tokenHashService.hash(rawRefreshToken);

        return repository.findAllByUserIdAndRevokedFalse(user.getId()).stream()
                .filter(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(token -> token.getTokenHash().equals(hashedToken))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    }

    protected void revoke(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        repository.save(refreshToken);
    }
}