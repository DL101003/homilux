package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.Permission;
import com.hoangloc.homilux.domain.Role;
import com.hoangloc.homilux.domain.dto.RoleDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.PermissionRepository;
import com.hoangloc.homilux.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public RoleDto createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new ResourceAlreadyExistsException("Vai trò", "name", role.getName());
        }

        List<Permission> permissions = role.getPermissions().stream()
                .map(p -> permissionRepository.findById(p.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Quyền", "ID", p.getId())))
                .collect(Collectors.toList());
        role.setPermissions(permissions);

        Role savedRole = roleRepository.save(role);
        return toDto(savedRole);
    }

    public RoleDto updateRole(Role updatedRole) {
        if (updatedRole.getId() == null) {
            throw new IllegalArgumentException("ID vai trò không được để trống!");
        }
        Role role = roleRepository.findById(updatedRole.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò", "ID", updatedRole.getId()));

        if (!role.getName().equals(updatedRole.getName()) &&
                roleRepository.existsByName(updatedRole.getName())) {
            throw new ResourceAlreadyExistsException("Vai trò", "name", updatedRole.getName());
        }

        role.setName(updatedRole.getName());

        if (updatedRole.getPermissions() != null) {
            List<Permission> permissions = updatedRole.getPermissions().stream()
                    .map(p -> permissionRepository.findById(p.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Quyền", "ID", p.getId())))
                    .collect(Collectors.toList());
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        return toDto(savedRole);
    }

    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò", "ID", id));
        if (!role.getUsers().isEmpty()) {
            throw new IllegalStateException("Không thể xóa vai trò đang được sử dụng bởi người dùng!");
        }
        roleRepository.deleteById(id); // Triggers soft delete via @SQLDelete
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

    private RoleDto toDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setPermissionIds(role.getPermissions().stream()
                .map(Permission::getId)
                .collect(Collectors.toList()));
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        return dto;
    }
}