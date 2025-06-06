package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.Permission;
import com.hoangloc.homilux.domain.dto.PermissionDto;
import com.hoangloc.homilux.domain.dto.ResultPaginationDTO;
import com.hoangloc.homilux.service.PermissionService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    public ResponseEntity<PermissionDto> createPermission(@Valid @RequestBody Permission permission) {
        PermissionDto createdPermission = permissionService.createPermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPermission);
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions(@Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(permissionService.getAll(spec, pageable));
    }

    @GetMapping("/permissions/{id}")
    public ResponseEntity<PermissionDto> getPermissionById(@PathVariable Long id) {
        PermissionDto permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(permission);
    }

    @PutMapping("/permissions")
    public ResponseEntity<PermissionDto> updatePermission(@RequestBody Permission permission) {
        PermissionDto updatedPermission = permissionService.updatePermission(permission);
        return ResponseEntity.ok(updatedPermission);
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}