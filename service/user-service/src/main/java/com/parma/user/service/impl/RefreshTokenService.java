package com.parma.user.service.impl;

import com.parma.common.constant.ApiConstant;
import com.parma.common.dto.EmptyObject;
import com.parma.common.exception.CustomMessageException;
import com.parma.user.model.RefreshToken;
import com.parma.user.model.User;
import com.parma.user.repository.RefreshTokenRepository;
import com.parma.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailService customUserDetailService;


    public void createRefreshToken(String username, String token, Date tokenExpiration) {
        Optional<User> user = userRepository.findFirstByUsernameAndStatus(username, ApiConstant.ACTIVE.getKey());
        if (user.isEmpty()) {
            log.error("User not found with username: {}", username);
            throw new CustomMessageException(
                    ApiConstant.USER_NAME_NOT_FOUND.getDescription(),
                    ApiConstant.USER_NAME_NOT_FOUND.getKey(),
                    new EmptyObject(),
                    HttpStatus.NOT_FOUND);
        }

        RefreshToken refreshToken;
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByUser(user.get());
        if(refreshTokenOptional.isPresent()) {
            log.info("Refresh refreshToken already exists..!");
            refreshToken = refreshTokenOptional.get();
            refreshToken.setToken(token);
            refreshToken.setExpiryDate(tokenExpiration);

        }else {
            refreshToken = RefreshToken.builder()
                    .user(user.get())
                    .token(token)
                    .expiryDate(tokenExpiration)
                    .build();
        }
        log.info("Refresh refreshToken created successfully..!");
        refreshTokenRepository.save(refreshToken);
    }



}
