package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.Permission;
import com.hoangloc.homilux.domain.dto.PermissionCreateDto;
import com.hoangloc.homilux.domain.dto.PermissionDto;
import com.hoangloc.homilux.domain.dto.PermissionUpdateDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public PermissionCreateDto createPermission(Permission permission) {
        if (permissionRepository.existsByName(permission.getName())) {
            throw new ResourceAlreadyExistsException("Quyền", "tên", permission.getName());
        }
        Permission savedPermission = permissionRepository.save(permission);
        return toCreateDto(savedPermission);
    }

    public PermissionDto getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quyền", "ID", id));
        return toDto(permission);
    }

    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PermissionUpdateDto updatePermission(Permission updatedPermission) {
        if (updatedPermission.getId() == null) {
            throw new IllegalArgumentException("ID quyền không được để trống!");
        }
        Permission permission = permissionRepository.findById(updatedPermission.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Quyền", "ID", updatedPermission.getId()));
        if (updatedPermission.getName() != null && !updatedPermission.getName().equals(permission.getName()) &&
                permissionRepository.existsByName(updatedPermission.getName())) {
            throw new ResourceAlreadyExistsException("Quyền", "tên", updatedPermission.getName());
        }
        if (updatedPermission.getName() != null) {
            permission.setName(updatedPermission.getName());
        }
        if (updatedPermission.getApiPath() != null) {
            permission.setApiPath(updatedPermission.getApiPath());
        }
        if (updatedPermission.getMethod() != null) {
            permission.setMethod(updatedPermission.getMethod());
        }
        if (updatedPermission.getModule() != null) {
            permission.setModule(updatedPermission.getModule());
        }
        Permission savedPermission = permissionRepository.save(permission);
        return toUpdateDto(savedPermission);
    }

    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quyền", "ID", id));
        permissionRepository.save(permission);
    }

    private PermissionDto toDto(Permission permission) {
        PermissionDto dto = new PermissionDto();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setApiPath(permission.getApiPath());
        dto.setMethod(permission.getMethod());
        dto.setModule(permission.getModule());
        dto.setCreatedAt(permission.getCreatedAt());
        dto.setUpdatedAt(permission.getUpdatedAt());
        return dto;
    }

    private PermissionCreateDto toCreateDto(Permission permission) {
        PermissionCreateDto dto = new PermissionCreateDto();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setApiPath(permission.getApiPath());
        dto.setMethod(permission.getMethod());
        dto.setModule(permission.getModule());
        dto.setCreatedAt(permission.getCreatedAt());
        return dto;
    }

    private PermissionUpdateDto toUpdateDto(Permission permission) {
        PermissionUpdateDto dto = new PermissionUpdateDto();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setApiPath(permission.getApiPath());
        dto.setMethod(permission.getMethod());
        dto.setModule(permission.getModule());
        dto.setUpdatedAt(permission.getUpdatedAt());
        return dto;
    }
}