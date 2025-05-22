package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.Role;
import com.hoangloc.homilux.domain.dto.ResultPaginationDTO;
import com.hoangloc.homilux.domain.dto.RoleDto;
import com.hoangloc.homilux.service.RoleService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody Role role) {
        RoleDto createdRole = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @GetMapping("/roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(roleService.getAll(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        RoleDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/roles")
    public ResponseEntity<RoleDto> updateRole(@RequestBody Role role) {
        RoleDto updatedRole = roleService.updateRole(role);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}