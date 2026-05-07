package com.mayis.auth_service.service;

import com.mayis.auth_service.dto.*;
import com.mayis.auth_service.model.entity.RefreshToken;
import com.mayis.auth_service.model.entity.Role;
import com.mayis.auth_service.model.entity.User;
import com.mayis.auth_service.model.enums.RoleName;
import com.mayis.auth_service.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(JwtService jwtService, AuthenticationManager authenticationManager, UserService userService, RoleService roleService, UserRoleService userRoleService, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {

        User user = userService.createUser(request);
        Role customerRole = roleService.getRoleByName(RoleName.CUSTOMER);
        userRoleService.create(new CreateUserRoleRequestDto(
                user.getId(),
                customerRole.getId()
        ));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.generateRefreshToken(new GenerateRefreshTokenRequestDto(
                user.getId()
        ));

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User userByUsername = userService.getUserByUsername(request.username());
        userByUsername.setFailedLoginAttempts(0);
        userByUsername.setLastLoginAt(LocalDateTime.now());

        String accessToken = jwtService.generateAccessToken(userByUsername);
        String refreshToken = refreshTokenService.generateRefreshToken(new GenerateRefreshTokenRequestDto(
                userByUsername.getId()
        ));

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    @Transactional
    public AuthResponseDto refresh(RefreshTokenRequestDto requestDto) {
        String rawRefreshToken = requestDto.refreshToken();

        if(!jwtService.isRefreshToken(rawRefreshToken)) {
            throw new RuntimeException("Invalid token type");
        }

        String username = jwtService.extractUsername(rawRefreshToken);
        User user = userService.getUserByUsername(username);

        RefreshToken storedToken = refreshTokenService.findValidRefreshToken(
                user,
                rawRefreshToken
        );
        
        refreshTokenService.revoke(storedToken);
        
        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.generateRefreshToken(
                new GenerateRefreshTokenRequestDto(user.getId())
        );
        
        return new AuthResponseDto(
                accessToken,
                newRefreshToken,
                "Bearer"
        );
    }
    
    @Transactional
    public void logout(LogoutRequestDto requestDto) {
        String rawRefreshToken = requestDto.refreshToken();

        if (!jwtService.isRefreshToken(rawRefreshToken)) {
            throw new RuntimeException("Invalid token type");
        }

        String username = jwtService.extractUsername(rawRefreshToken);

        User user = userService.getUserByUsername(username);

        RefreshToken storedToken = refreshTokenService.findValidRefreshToken(
                user,
                rawRefreshToken
        );

        refreshTokenService.revoke(storedToken);
    }
}