package com.hoangloc.homilux.services;

import com.hoangloc.homilux.dtos.permissionDto.PermissionResponse;
import com.hoangloc.homilux.entities.Permission;
import com.hoangloc.homilux.repositories.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "permissionList", key = "'all'")
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private PermissionResponse mapToResponse(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getName(),
                permission.getApiPath(),
                permission.getMethod().toString(),
                permission.getModule()
        );
    }
}