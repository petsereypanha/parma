package com.parma.user.service;

import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.dto.request.AuthenticationRequest;

public interface AuthService {
    ResponseErrorTemplate login(AuthenticationRequest authenticationRequest);
}
