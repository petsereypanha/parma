package com.parma.user.service.impl;

import com.parma.common.constant.ApiConstant;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.common.exception.SystemException;
import com.parma.user.constant.Constant;
import com.parma.user.dto.request.CreatePermissionRequest;
import com.parma.user.dto.response.PermissionResponse;
import com.parma.user.model.Permission;
import com.parma.user.model.Role;
import com.parma.user.repository.PermissionRepository;
import com.parma.user.repository.RoleRepository;
import com.parma.user.service.PermissionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public ResponseErrorTemplate create(CreatePermissionRequest request) {
        // check permission is already exists
        if (permissionRepository.existsByName(request.name())) {
            throw new SystemException("Permission with name '" + request.name() + "' already exists");
        }
        // Convert request to Permission entity
        Permission permission = convertRequestToPermission(request);
        permissionRepository.save(permission);
        return convertPermissionResponse(permission);
    }

    @Override
    public ResponseErrorTemplate update(Long id, CreatePermissionRequest request) {
        // Validate that the permission exists
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new SystemException("Permission with id '" + id + "' not found"));
        // Check Permission already exists
        if (!permission.getName().equals(request.name()) && permissionRepository.existsByName(request.name())) {
            throw new SystemException("Permission with name '" + request.name() + "' already exists");

        }
        // Update the permission details
        permission.setName(request.name());
        permission.setDescription(request.description());
        permission.setStatus(request.status());
        // Save the updated permission
        permissionRepository.save(permission);
        return convertPermissionResponse(permission);
    }

    @Override
    public ResponseErrorTemplate assignRoleToPermission(Long permissionId, Long roleId) {
        // check permission not exists
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new SystemException("Permission with id '" + permissionId + "' not found"));
        // check role not exists
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new SystemException("Role with id '" + roleId + "' not found"));
        permission.addRole(role);
        permissionRepository.save(permission);
        return convertPermissionResponse(permission);
    }

    @Override
    public ResponseErrorTemplate removeRoleFromPermission(Long permissionId, Long roleId) {
        // check permission not exists
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new SystemException("Permission with id '" + permissionId + "' not found"));
        // check role not exists
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new SystemException("Role with id '" + roleId + "' not found"));
        permission.removeRole(role);
        permissionRepository.save(permission);
        return convertPermissionResponse(permission);
    }

    @Override
    public List<Permission> getPermissionsByNameIn(Set<String> names) {
        return permissionRepository.findAllByStatusAndNameIn(Constant.ACTIVE, names);
    }
    // Convert CreatePermissionRequest to Permission entity using in the service layer
    private Permission convertRequestToPermission(CreatePermissionRequest request) {
        Permission permission = new Permission();
        permission.setName(request.name());
        permission.setDescription(request.description());
        permission.setStatus(Constant.ACTIVE);
        return permission;
    }
    // Convert Permission entity to PermissionResponse for API response
    private ResponseErrorTemplate convertPermissionResponse(Permission permission) {
        PermissionResponse permissionResponse = new PermissionResponse(
                permission.getId(),
                permission.getName(),
                permission.getDescription(),
                permission.getStatus(),
                permission.getRoles(),
                permission.getGroups()
        );

        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                permissionResponse,
                false
        );
    }
}
