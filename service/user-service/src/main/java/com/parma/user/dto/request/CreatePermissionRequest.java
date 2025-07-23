package com.parma.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.parma.user.model.Group;
import com.parma.user.model.Role;
import lombok.Data;

import java.util.Set;

public record CreatePermissionRequest(
        Long id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        String status,
        @JsonProperty("roles") Set<Role> roles,
        @JsonProperty("groups") Set<Group> groups
){}

