package com.parma.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public record RoleRequest(
        @JsonProperty("id") Long id,
        @JsonProperty("name")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,
        @JsonProperty("code")
        @Size(max = 20, message = "Code must not exceed 20 characters")
        @Pattern(regexp = "^[A-Z0-9_]*$", message = "Code can only contain uppercase letters, numbers, and underscores")
        String code,
        @JsonProperty("description")
        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,
        @JsonProperty("status")
        @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either ACTIVE or INACTIVE")
        String status,
        @JsonProperty("created_by") String createdBy,
        @JsonProperty("updated_by") String updatedBy
) { }
