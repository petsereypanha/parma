package com.parma.user.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GroupMemberRequest(
    @NotNull(message = "User IDs are required")
    List<Long> userIds
) { }