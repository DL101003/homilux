package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.Permission;
import com.hoangloc.homilux.domain.Role;
import com.hoangloc.homilux.domain.dto.RoleCreateDto;
import com.hoangloc.homilux.domain.dto.RoleDto;
import com.hoangloc.homilux.domain.dto.RoleUpdateDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.PermissionRepository;
import com.hoangloc.homilux.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public RoleCreateDto createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new ResourceAlreadyExistsException("Vai trò", "tên", role.getName());
        }
        Set<Permission> permissions = role.getPermissions().stream()
                .map(permission -> permissionRepository.findById(permission.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Quyền", "ID", permission.getId())))
                .collect(Collectors.toSet());
        role.setPermissions(permissions);
        Role savedRole = roleRepository.save(role);
        return toCreateDto(savedRole);
    }

    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò", "ID", id));
        return toDto(role);
    }

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RoleUpdateDto updateRole(Role updatedRole) {
        if (updatedRole.getId() == null) {
            throw new IllegalArgumentException("ID vai trò không được để trống!");
        }
        Role role = roleRepository.findById(updatedRole.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò", "ID", updatedRole.getId()));
        if (updatedRole.getName() != null && !updatedRole.getName().equals(role.getName()) &&
                roleRepository.existsByName(updatedRole.getName())) {
            throw new ResourceAlreadyExistsException("Vai trò", "tên", updatedRole.getName());
        }
        if (updatedRole.getName() != null) {
            role.setName(updatedRole.getName());
        }
        if (updatedRole.getPermissions() != null) {
            Set<Permission> permissions = updatedRole.getPermissions().stream()
                    .map(permission -> permissionRepository.findById(permission.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Quyền", "ID", permission.getId())))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }
        Role savedRole = roleRepository.save(role);
        return toUpdateDto(savedRole);
    }

    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò", "ID", id));
        roleRepository.save(role);
    }

    private RoleDto toDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setPermissionIds(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet()));
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        return dto;
    }

    private RoleCreateDto toCreateDto(Role role) {
        RoleCreateDto dto = new RoleCreateDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setPermissionIds(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet()));
        dto.setCreatedAt(role.getCreatedAt());
        return dto;
    }

    private RoleUpdateDto toUpdateDto(Role role) {
        RoleUpdateDto dto = new RoleUpdateDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setPermissionIds(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet()));
        dto.setUpdatedAt(role.getUpdatedAt());
        return dto;
    }
}