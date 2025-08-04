package com.parma.user.controller;

import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.dto.request.CreateGroupRequest;
import com.parma.user.dto.request.GroupMemberRequest;
import com.parma.user.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<ResponseErrorTemplate> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(groupService.createGroup(request));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ResponseErrorTemplate> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(groupService.updateGroup(groupId, request));
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<ResponseErrorTemplate> addMembersToGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupMemberRequest request) {
        return ResponseEntity.ok(groupService.addMembersToGroup(groupId, request));
    }

    @DeleteMapping("/{groupId}/members")
    public ResponseEntity<ResponseErrorTemplate> removeMembersFromGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupMemberRequest request) {
        return ResponseEntity.ok(groupService.removeMembersFromGroup(groupId, request));
    }
}
