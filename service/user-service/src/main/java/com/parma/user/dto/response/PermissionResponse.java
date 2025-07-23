package com.parma.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.parma.user.model.Group;
import com.parma.user.model.Role;
import lombok.Data;

import java.util.Set;

public record PermissionResponse(
    Long id,

    @JsonProperty("name")
    String name,

    @JsonProperty("description")
    String description,

    String status,

    @JsonProperty("roles")
    Set<Role> roles,

    @JsonProperty("groups")
    Set<Group> groups
) { }