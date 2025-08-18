package com.parma.user.controller;


import com.parma.common.constant.ApiConstant;
import com.parma.common.dto.EmptyObject;
import com.parma.common.dto.UserRequest;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.dto.request.AuthenticationRequest;
import com.parma.user.dto.request.RefreshTokenRequest;
import com.parma.user.service.AuthService;
import com.parma.user.service.JwtService;
import com.parma.user.service.UserService;
import com.parma.user.service.impl.CustomUserDetailService;
import com.parma.user.service.impl.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/public/users")
@RequiredArgsConstructor
public class PublicController {

    private final AuthService authService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailService customUserDetailService;
    private final JwtService jwtService;

    @PostMapping("/registration")
    public ResponseEntity<ResponseErrorTemplate> register(@RequestBody UserRequest userRequest) {
        log.info("Intercept registration new user with req: {}", userRequest);
        return ResponseEntity.ok(userService.create(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseErrorTemplate> login(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authService.login(authenticationRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseErrorTemplate> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Intercept logout refresh token with req: {}", refreshTokenRequest);
        refreshTokenService.deleteToken(refreshTokenRequest.refreshToken());
        var responseErrorTemplate = new ResponseErrorTemplate(
                ApiConstant.LOGOUT_SUCCESS.getDescription(),
                ApiConstant.LOGOUT_SUCCESS.getKey(),
                new EmptyObject(),
                false);
        return ResponseEntity.ok(responseErrorTemplate);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseErrorTemplate> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(refreshTokenService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/verify-token")
    public ResponseEntity<ResponseErrorTemplate> verifyToken(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("Intercept token verification request");

        var responseErrorTemplate = jwtService.verifyToken(authorizationHeader);
        if (responseErrorTemplate.isError()) {
            return ResponseEntity.status(401).body(responseErrorTemplate);
        }
        return ResponseEntity.status(200).body(responseErrorTemplate);
    }

}