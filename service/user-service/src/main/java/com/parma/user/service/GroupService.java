package com.parma.user.service;

import com.parma.user.dto.request.CreateGroupRequest;
import com.parma.user.dto.request.GroupMemberRequest;
import com.parma.user.dto.response.GroupResponse;

public interface GroupService {

    GroupResponse createGroup(CreateGroupRequest request);

    GroupResponse updateGroup(Long id, CreateGroupRequest request);

    GroupResponse addMembersToGroup(Long groupId, GroupMemberRequest groupMemberRequest);

    GroupResponse removeMembersFromGroup(Long groupId, GroupMemberRequest groupMemberRequest);
}
