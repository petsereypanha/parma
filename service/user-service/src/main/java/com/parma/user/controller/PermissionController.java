package com.parma.user.controller;

import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.dto.request.CreatePermissionRequest;
import com.parma.user.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<ResponseErrorTemplate> createPermission(@RequestBody CreatePermissionRequest permission) {
        return ResponseEntity.ok(permissionService.create(permission));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> updatePermission(@PathVariable Long id,
                                                                  @RequestBody CreatePermissionRequest permissionDetails) {
        return ResponseEntity.ok(permissionService.update(id, permissionDetails));
    }

    @PostMapping("/{permissionId}/roles/{roleId}")
    public ResponseEntity<ResponseErrorTemplate> assignRoleToPermission(@PathVariable Long permissionId,@PathVariable Long roleId) {
        return ResponseEntity.ok(permissionService.assignRoleToPermission(permissionId, roleId));
    }

    @DeleteMapping("/{permissionId}/roles/{roleId}")
    public ResponseEntity<ResponseErrorTemplate> removeRoleFromPermission(@PathVariable Long permissionId,@PathVariable Long roleId) {
        return ResponseEntity.ok(permissionService.removeRoleFromPermission(permissionId, roleId));
    }

}
