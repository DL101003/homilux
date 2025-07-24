package com.hoangloc.homilux.services;

import com.hoangloc.homilux.dtos.permissionDto.PermissionResponse;
import com.hoangloc.homilux.dtos.roleDto.RoleRequest;
import com.hoangloc.homilux.dtos.roleDto.RoleResponse;
import com.hoangloc.homilux.entities.Permission;
import com.hoangloc.homilux.entities.Role;
import com.hoangloc.homilux.exceptions.DuplicateResourceException;
import com.hoangloc.homilux.exceptions.InvalidRequestException;
import com.hoangloc.homilux.exceptions.ResourceNotFoundException;
import com.hoangloc.homilux.repositories.PermissionRepository;
import com.hoangloc.homilux.repositories.RoleRepository;
import com.hoangloc.homilux.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Role with name '" + request.name() + "' already exists.");
        }

        List<Permission> permissions = validateAndLoadPermissions(request.permissionIds());

        Role role = Role.builder()
                .name(request.name())
                .permissions(permissions)
                .build();

        Role savedRole = roleRepository.save(role);
        log.info("Created new role with id: {}", savedRole.getId());
        return mapToResponse(savedRole);
    }

    
    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));

        roleRepository.findByName(request.name()).ifPresent(existingRole -> {
            if (!existingRole.getId().equals(role.getId())) {
                throw new DuplicateResourceException("Role name '" + request.name() + "' is already taken by another role.");
            }
        });

        List<Permission> permissions = validateAndLoadPermissions(request.permissionIds());

        role.setName(request.name());
        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);

        Role updatedRole = roleRepository.save(role);
        log.info("Updated role with id: {}", updatedRole.getId());
        return mapToResponse(updatedRole);
    }

    
    @Transactional
    public void deleteRole(Long id) {
        // 1. Kiểm tra Role có tồn tại không
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", id);
        }

        // 2. Kiểm tra xem có user nào đang sử dụng role này không
        if (userRepository.existsByRoleId(id)) {
            throw new InvalidRequestException("Cannot delete role. It is currently assigned to one or more users.");
        }

        // 3. Xóa role (quan hệ role_permissions sẽ được xóa tự động nhờ Cascade hoặc JoinTable)
        roleRepository.deleteById(id);
        log.info("Deleted role with id: {}", id);
    }

    
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        return mapToResponse(role);
    }

    
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        List<Role> rolePage = roleRepository.findAll();
        return rolePage.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private List<Permission> validateAndLoadPermissions(List<Long> permissionIds) {
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            // Tìm ra ID không hợp lệ để báo lỗi chi tiết hơn
            List<Long> foundIds = permissions.stream().map(Permission::getId).toList();
            List<Long> notFoundIds = permissionIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("Permission", notFoundIds.toString());
        }
        return permissions;
    }

    private RoleResponse mapToResponse(Role role) {
        List<PermissionResponse> permissionResponses = role.getPermissions().stream()
                .map(permission -> new PermissionResponse(
                        permission.getId(),
                        permission.getName(),
                        permission.getApiPath(),
                        permission.getMethod().toString(),
                        permission.getModule()))
                .toList();

        return new RoleResponse(
                role.getId(),
                role.getName(),
                permissionResponses
        );
    }
}