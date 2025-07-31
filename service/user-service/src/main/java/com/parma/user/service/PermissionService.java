package com.parma.user.service;

import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.dto.request.CreatePermissionRequest;
import com.parma.user.dto.response.PermissionResponse;
import com.parma.user.model.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService {

    ResponseErrorTemplate create(CreatePermissionRequest request);

    ResponseErrorTemplate update(Long id, CreatePermissionRequest request);

    ResponseErrorTemplate assignRoleToPermission(Long permissionId, Long roleId);

    ResponseErrorTemplate removeRoleFromPermission(Long permissionId, Long roleId);

    List<Permission> getPermissionsByNameIn(Set<String> names);
}
