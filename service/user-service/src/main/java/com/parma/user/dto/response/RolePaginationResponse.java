package com.parma.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.parma.common.dto.Metadata;
import com.parma.common.dto.PaginationResponse;

import java.util.List;

public record RolePaginationResponse(
        @JsonProperty("items") List<RoleResponse> responses,
        @JsonProperty("page") PaginationResponse paginationResponse,
        @JsonProperty("metadata") Metadata metadata
) { }
