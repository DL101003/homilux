package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.annotation.ApiMessage;
import com.hoangloc.homilux.domain.Permission;
import com.hoangloc.homilux.domain.dto.PermissionCreateDto;
import com.hoangloc.homilux.domain.dto.PermissionDto;
import com.hoangloc.homilux.domain.dto.PermissionUpdateDto;
import com.hoangloc.homilux.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<PermissionCreateDto> createPermission(@Valid @RequestBody Permission permission) {
        PermissionCreateDto createdPermission = permissionService.createPermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPermission);
    }

    @GetMapping("/permissions")
    @ApiMessage("Get all permissions")
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        List<PermissionDto> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Get a permission by id")
    public ResponseEntity<PermissionDto> getPermissionById(@PathVariable Long id) {
        PermissionDto permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(permission);
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<PermissionUpdateDto> updatePermission(@RequestBody Permission permission) {
        PermissionUpdateDto updatedPermission = permissionService.updatePermission(permission);
        return ResponseEntity.ok(updatedPermission);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission by id")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}