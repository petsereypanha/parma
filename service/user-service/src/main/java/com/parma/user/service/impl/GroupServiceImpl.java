package com.parma.user.service.impl;

import com.parma.common.constant.ApiConstant;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.constant.Constant;
import com.parma.user.dto.request.*;
import com.parma.user.dto.response.GroupResponse;
import com.parma.user.model.*;
import com.parma.user.repository.*;
import com.parma.user.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private  final GroupRepository groupRepository;
    private  final PermissionService permissionService;


    @Override
    public ResponseErrorTemplate createGroup(CreateGroupRequest request) {
        // Check if group with the same name already exists
        if (groupRepository.existsByName(request.name())) {
            throw new RuntimeException("Group with name " + request.name() + " already exists");
        }
        Set<Role> roles = request.roles() != null ? new HashSet<>(roleRepository.findByNameIn(request.roles())) : Set.of();
        Set<Permission> permissions = request.permissions() != null ? new HashSet<>(permissionService.getPermissionsByNameIn(request.permissions())) : Set.of();
        Group group =convertRequestToGroup(request);
        group.setRoles(roles);
        group.setPermissions(permissions);
        groupRepository.save(group);
        return convertGroupToResponse(group);
    }

    @Override
    public ResponseErrorTemplate updateGroup(Long id, CreateGroupRequest request) {
        // check group is not exists
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group with id " + id + " not found"));
        // Check if group with the same name already exists
        final String groupName = request.name();
        if (groupName != null && !groupName.equals(group.getName())) {
            if (groupRepository.existsByName(groupName)) {
                throw new RuntimeException("Group with name '" + groupName + "' already exists");
            }
            group.setName(groupName);
        }

        if (request.description() != null) {
            group.setDescription(request.description());
        }
        if (request.roles() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findByNameIn(request.roles()));
            group.setRoles(roles);
        }
        if (request.permissions() != null) {
            Set<Permission> permissions = new HashSet<>(permissionService.getPermissionsByNameIn(request.permissions()));
            group.setPermissions(permissions);
        }
        if (StringUtils.hasText(request.status())) {
            group.setStatus(request.status());
        }
        groupRepository.save(group);
        return convertGroupToResponse(group);
    }

    @Override
    public ResponseErrorTemplate addMembersToGroup(Long groupId, GroupMemberRequest groupMemberRequest) {
        // check group is not exists
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        List<User> users = userRepository.findAllById(groupMemberRequest.userIds());

        for (User user : users) {
            if (!group.getUsers().contains(user)) {
                group.addUser(user);
            }
        }
        groupRepository.save(group);
        return convertGroupToResponse(group);
    }

    @Override
    public ResponseErrorTemplate removeMembersFromGroup(Long groupId, GroupMemberRequest groupMemberRequest) {
        // check group is not exists
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        List<User> users = userRepository.findAllById(groupMemberRequest.userIds());
        for (User user : users) {
            group.removeUser(user);
        }
        Group updatedGroup = groupRepository.save(group);
        return convertGroupToResponse(updatedGroup);
    }
    // Convert Group Request to Group entity
    private Group convertRequestToGroup(CreateGroupRequest request) {
        Group group = new Group();
        group.setName(request.name());
        group.setDescription(request.description());
        group.setStatus(Constant.ACTIVE);

        return group;
    }
    // Convert Group entity to Group Response
    private ResponseErrorTemplate convertGroupToResponse(Group group) {
        GroupResponse groupResponse = new  GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()),
                group.getPermissions().stream().map(Permission::getName).collect(java.util.stream.Collectors.toSet()),
                group.getStatus(),
                group.getCreatedBy(),
                group.getUpdatedBy(),
                group.getCreatedAt(),
                group.getUpdatedAt(),
                group.getUsers().size()
        );
        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                groupResponse,
                false
        );

    }
}
