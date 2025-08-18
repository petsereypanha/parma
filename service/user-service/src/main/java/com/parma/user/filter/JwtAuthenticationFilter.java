package com.parma.user.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parma.common.constant.ApiConstant;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.dto.request.AuthenticationRequest;
import com.parma.user.dto.response.AuthenticationResponse;
import com.parma.user.model.CustomUserDetail;
import com.parma.user.service.JwtService;
import com.parma.user.service.impl.CustomUserDetailService;
import com.parma.user.utils.CustomMessageExceptionUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Collections;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailService customUserDetailService;

    public JwtAuthenticationFilter(JwtService jwtService,ObjectMapper objectMapper, String jwtConfigUrl, AuthenticationManager authenticationManager, CustomUserDetailService customUserDetailService) {
        super(new AntPathRequestMatcher(jwtConfigUrl, "POST"));
        setAuthenticationManager(authenticationManager);
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetail customUserDetail = (CustomUserDetail) authResult.getPrincipal();
        var accessToken = jwtService.generateToken(customUserDetail);
        var refreshToken = jwtService.refreshToken(customUserDetail);
        customUserDetailService.updateAttempt(customUserDetail.getUsername());

        AuthenticationResponse authenticationResponse = new AuthenticationResponse(
                accessToken,
                refreshToken
        );

        var responseErrorTemplate = new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                authenticationResponse,
                false
        );
        var jsonUser = objectMapper.writeValueAsString(responseErrorTemplate);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonUser);
        log.info("Successful Authentication {}", authenticationResponse);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("Start attempt to authentication");
        AuthenticationRequest authenticationRequest = objectMapper.readValue(request.getInputStream(),
                AuthenticationRequest.class);

        customUserDetailService.saveUserAttemptAuthentication(authenticationRequest.username());
        log.info("End attempt to authentication");
        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(
                        authenticationRequest.username(),
                        authenticationRequest.password(),
                        Collections.emptyList())
                );
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        var messageException = CustomMessageExceptionUtils.unauthorized();
        var msgJson = objectMapper.writeValueAsString(messageException);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(msgJson);
        log.info("Unsuccessful Authentication {}", failed.getLocalizedMessage());
    }
}
