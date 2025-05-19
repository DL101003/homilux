package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.annotation.ApiMessage;
import com.hoangloc.homilux.domain.Role;
import com.hoangloc.homilux.domain.dto.RoleCreateDto;
import com.hoangloc.homilux.domain.dto.RoleDto;
import com.hoangloc.homilux.domain.dto.RoleUpdateDto;
import com.hoangloc.homilux.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<RoleCreateDto> createRole(@Valid @RequestBody Role role) {
        RoleCreateDto createdRole = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @GetMapping("/roles")
    @ApiMessage("Get all roles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Get a role by id")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        RoleDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<RoleUpdateDto> updateRole(@RequestBody Role role) {
        RoleUpdateDto updatedRole = roleService.updateRole(role);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role by id")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}