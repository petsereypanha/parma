package com.parma.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
    String token,

    @JsonProperty("token_type")
    String tokenTye,
    String username,
    String role
) { }
