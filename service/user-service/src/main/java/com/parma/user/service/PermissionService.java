package com.parma.user.service;

import com.parma.user.dto.request.CreatePermissionRequest;
import com.parma.user.dto.response.PermissionResponse;
import com.parma.user.model.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService {

    PermissionResponse create(CreatePermissionRequest request);

    PermissionResponse update(Long id, CreatePermissionRequest request);

    PermissionResponse assignRoleToPermission(Long permissionId, Long roleId);

    PermissionResponse removeRoleFromPermission(Long permissionId, Long roleId);

    List<Permission> getPermissionsByNameIn(Set<String> names);
}
