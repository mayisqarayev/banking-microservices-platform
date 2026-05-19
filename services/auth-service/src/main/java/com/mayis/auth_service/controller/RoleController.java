package com.mayis.auth_service.controller;

import com.mayis.auth_service.model.enums.RoleName;
import com.mayis.auth_service.service.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleName> getAll() {
        return roleService.getAll();
    }
}
