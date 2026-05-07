package com.mayis.auth_service.controller;

import com.mayis.auth_service.dto.UserResponseDto;
import com.mayis.auth_service.model.entity.User;
import com.mayis.auth_service.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}