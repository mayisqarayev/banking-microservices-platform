package com.mayis.auth_service.service;

import com.mayis.auth_service.dto.*;
import com.mayis.auth_service.exception.InvalidTokenTypeException;
import com.mayis.auth_service.event.UserEventPublisher;
import com.mayis.auth_service.event.UserRegisteredEvent;
import com.mayis.auth_service.model.entity.RefreshToken;
import com.mayis.auth_service.model.entity.Role;
import com.mayis.auth_service.model.entity.User;
import com.mayis.auth_service.model.enums.RoleName;
import com.mayis.auth_service.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final RefreshTokenService refreshTokenService;
    private final UserEventPublisher userEventPublisher;

    public AuthService(
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            UserService userService,
            RoleService roleService,
            UserRoleService userRoleService,
            RefreshTokenService refreshTokenService,
            UserEventPublisher userEventPublisher
    ) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
        this.refreshTokenService = refreshTokenService;
        this.userEventPublisher = userEventPublisher;
    }

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {

        User user = userService.createUser(request);
        Role customerRole = roleService.getRoleByName(RoleName.CUSTOMER);
        userRoleService.create(new CreateUserRoleRequestDto(
                user.getId(),
                customerRole.getId()
        ));
        User registeredUser = userService.getUserByUsername(user.getUsername());
        userEventPublisher.publishUserRegistered(
                new UserRegisteredEvent(
                        UUID.randomUUID(),
                        LocalDateTime.now(),
                        registeredUser.getId(),
                        registeredUser.getUsername(),
                        registeredUser.getEmail(),
                        registeredUser.getFirstName(),
                        registeredUser.getLastName(),
                        Set.of(customerRole.getAuthority())
                )
        );

        String accessToken = jwtService.generateAccessToken(registeredUser);
        String refreshToken = refreshTokenService.generateRefreshToken(new GenerateRefreshTokenRequestDto(
                registeredUser.getId()
        ));

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException ex) {
            userService.handleFailedLogin(request.username());
            throw ex;
        }


        User userByUsername = userService.getUserByUsername(request.username());
        userService.handleSuccessfulLogin(userByUsername);

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
            throw new InvalidTokenTypeException("Invalid token type");
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
            throw new InvalidTokenTypeException("Invalid token type");
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
