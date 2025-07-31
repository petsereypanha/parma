package com.parma.user.service;

import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.dto.request.CreateGroupRequest;
import com.parma.user.dto.request.GroupMemberRequest;
import com.parma.user.dto.response.GroupResponse;

public interface GroupService {

    ResponseErrorTemplate createGroup(CreateGroupRequest request);

    ResponseErrorTemplate updateGroup(Long id, CreateGroupRequest request);

    ResponseErrorTemplate addMembersToGroup(Long groupId, GroupMemberRequest groupMemberRequest);

    ResponseErrorTemplate removeMembersFromGroup(Long groupId, GroupMemberRequest groupMemberRequest);
}
