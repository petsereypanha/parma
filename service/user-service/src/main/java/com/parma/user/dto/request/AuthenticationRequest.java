package com.parma.user.dto.request;

public record AuthenticationRequest(
    String username,
    String password
) { }
