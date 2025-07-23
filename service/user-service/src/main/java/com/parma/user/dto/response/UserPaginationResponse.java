package com.parma.user.dto.response;

import com.fasterxml.jackson.annotation.*;
import com.parma.common.dto.*;

import java.util.*;

public record UserPaginationResponse(
        @JsonProperty("items") List<UserResponse> userResponses,
        @JsonProperty("page") PaginationResponse paginationResponse,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("metadata") Map<String, Object> metadata) {

    public UserPaginationResponse(List<UserResponse> userResponses, PaginationResponse paginationResponse) {
        this(userResponses, paginationResponse, null);
    }

    public UserPaginationResponse withMetadata(Map<String, Object> metadata) {
        return new UserPaginationResponse(userResponses, paginationResponse, metadata);
    }

    public UserPaginationResponse withMetadata(String key, Object value) {
        Map<String, Object> newMetadata = metadata != null ? new java.util.HashMap<>(metadata) : new java.util.HashMap<>();
        newMetadata.put(key, value);
        return new UserPaginationResponse(userResponses, paginationResponse, newMetadata);
    }
}
