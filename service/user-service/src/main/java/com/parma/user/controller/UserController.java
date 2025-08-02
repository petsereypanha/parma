package com.parma.user.controller;

import com.parma.common.dto.UserRequest;
import com.parma.common.exception.GeneralErrorResponse;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.dto.request.UserChangePasswordRequest;
import com.parma.user.dto.request.UserFilterRequest;
import com.parma.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@Controller
@RequestMapping("api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/account")
    public ResponseEntity<Object> getUserAccount(Principal principal) {
        var username = principal.getName();
        log.info("Fetching account details for user: {}", username);
        return ResponseEntity.ok(userService.findByUsername(username));
    }
    @PostMapping("/filter")
    public ResponseEntity<ResponseErrorTemplate> findUser(@Validated @RequestBody UserFilterRequest userFilterRequest){
        log.info("Intercept filter users with : {}", userFilterRequest);
        try {
            // Validate pagination parameters
            if (userFilterRequest.getPageNumber() < 0) {
                return new ResponseEntity<>(
                        new ResponseErrorTemplate(
                                "Page number must be greater than or equal to 0",
                                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                                new Object(),
                                true
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }
            if (userFilterRequest.getPageSize() < 1) {
                return new ResponseEntity<>(
                        new ResponseErrorTemplate(
                                "Page size must be greater than 0",
                                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                                new Object(),
                                true
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }
            // Validate date range if provided
            if (userFilterRequest.hasDateRange() &&
                    userFilterRequest.getStartDate().isAfter(userFilterRequest.getEndDate())) {
                return new ResponseEntity<>(
                        new ResponseErrorTemplate(
                                "Start date cannot be after end date",
                                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                                new Object(),
                                true
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }
            return new ResponseEntity<>(
                    userService.findAll(userFilterRequest),
                    HttpStatus.OK
            );
        } catch (IllegalArgumentException e){
            log.error("Invalid request parameters: {}", e.getMessage());
            return new ResponseEntity<>(
                    new ResponseErrorTemplate(
                            e.getMessage(),
                            String.valueOf(HttpStatus.BAD_REQUEST.value()),
                            new Object(),
                            true
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
        catch (Exception e) {
            log.error("Error filtering users: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                    GeneralErrorResponse.generalErrors(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @GetMapping("{/id}")
    public ResponseEntity<ResponseErrorTemplate> getUserById(@PathVariable Long id) {
        log.info("Intercept check user by ID: {}", id);
        try{
            return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching user by ID: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                    GeneralErrorResponse.generalErrors(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @PostMapping
    public ResponseEntity<ResponseErrorTemplate> createUser(@Validated @RequestBody UserRequest userRequest) {
        log.info("Intercept create user with request: {}", userRequest);
        try {
            return new ResponseEntity<>(userService.create(userRequest), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                    GeneralErrorResponse.generalErrors(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @PutMapping("{/id}")
    public ResponseEntity<ResponseErrorTemplate> update(@PathVariable Long id, @Validated @RequestBody UserRequest userRequest) {
        log.info("Intercept update user with ID: {} and request: {}", id, userRequest);
        try {
            return new ResponseEntity<>(userService.update(id, userRequest), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                    GeneralErrorResponse.generalErrors(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @PostMapping("/{id}/change/password")
    public ResponseEntity<ResponseErrorTemplate> changePassword(@PathVariable Long id, @Validated @RequestBody UserChangePasswordRequest request) {
        log.info("Intercept change password user with ID: {} and request: {}", id, request);
        try {
            return new ResponseEntity<>(
                    userService.changePassword(id, request),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Error changing password user: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                    GeneralErrorResponse.generalErrors(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @DeleteMapping("/{ids}/status/{status}")
    public ResponseEntity<ResponseErrorTemplate> disActivateUser(@PathVariable Set<Long> ids, @PathVariable String status) {
        log.info("Intercept disActivate user with IDs: {} and status: {}", ids, status);
        try {
            return new ResponseEntity<>(
                    userService.disActivateUser(ids, status),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Error deactivating users: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                    GeneralErrorResponse.generalErrors(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @GetMapping("/{ids}/password/reset")
    public ResponseEntity<ResponseErrorTemplate> resetPassword(@PathVariable Set<Long> ids) {
        log.info("Intercept reset password for user IDs: {}", ids);
        try {
            return new ResponseEntity<>(
                    userService.resetPassword(ids),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Error resetting password for users: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                    GeneralErrorResponse.generalErrors(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
