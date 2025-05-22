package com.hoangloc.homilux.service;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.domain.Permission;
import com.hoangloc.homilux.domain.dto.PermissionDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.PermissionRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionService extends AbstractPaginationService<Permission> {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        super(permissionRepository);
        this.permissionRepository = permissionRepository;
    }

    public PermissionDto createPermission(Permission permission) {
        if (permissionRepository.existsByName(permission.getName())) {
            throw new ResourceAlreadyExistsException("Quyền", "tên", permission.getName());
        }
        if (permissionRepository.existsByApiPathAndMethod(permission.getApiPath(), permission.getMethod())) {
            throw new ResourceAlreadyExistsException("Quyền", "apiPath và method", permission.getApiPath() + ", " + permission.getMethod());
        }
        Permission savedPermission = permissionRepository.save(permission);
        return toDto(savedPermission);
    }

    public PermissionDto updatePermission(Permission updatedPermission) {
        if (updatedPermission.getId() == null) {
            throw new IllegalArgumentException("ID quyền không được để trống!");
        }
        Permission permission = permissionRepository.findById(updatedPermission.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Quyền", "ID", updatedPermission.getId()));

        if (!permission.getName().equals(updatedPermission.getName()) &&
                permissionRepository.existsByName(updatedPermission.getName())) {
            throw new ResourceAlreadyExistsException("Quyền", "tên", updatedPermission.getName());
        }
        if (!permission.getApiPath().equals(updatedPermission.getApiPath()) ||
                !permission.getMethod().equals(updatedPermission.getMethod())) {
            if (permissionRepository.existsByApiPathAndMethod(updatedPermission.getApiPath(), updatedPermission.getMethod())) {
                throw new ResourceAlreadyExistsException("Quyền", "apiPath và method", updatedPermission.getApiPath() + ", " + updatedPermission.getMethod());
            }
        }

        permission.setName(updatedPermission.getName());
        permission.setApiPath(updatedPermission.getApiPath());
        permission.setMethod(updatedPermission.getMethod());
        permission.setModule(updatedPermission.getModule());

        Permission savedPermission = permissionRepository.save(permission);
        return toDto(savedPermission);
    }

    public void deletePermission(Long id) {
        permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quyền", "ID", id));
        permissionRepository.deleteById(id); // Triggers soft delete via @SQLDelete
    }

    public PermissionDto getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quyền", "ID", id));
        return toDto(permission);
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
}