package com.parma.user.service.impl;

import com.parma.common.constant.ApiConstant;
import com.parma.common.dto.EmptyObject;
import com.parma.common.dto.PageableResponse;
import com.parma.common.dto.UserRequest;
import com.parma.common.dto.UserResponse;
import com.parma.common.exception.BusinessException;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.constant.Constant;
import com.parma.user.dto.request.*;
import com.parma.user.dto.response.UserPaginationResponse;
import com.parma.user.exception.UserValidationException;
import com.parma.user.model.User;
import com.parma.user.repository.*;
import com.parma.user.service.*;
import com.parma.user.service.handler.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserHandlerService userHandlerService;
    private final UserSearchService userSearchService;
    private final PageableResponseHandlerService pageableResponseHandlerService;

    @Value("${jwt.default-password:'mypwd@123'}")
    private String defaultPassword;

    @Override
    @Transactional
    public ResponseErrorTemplate create(UserRequest request) {
        ResponseErrorTemplate errorTemplate =userHandlerService.userRequestValidation(request);
        if (errorTemplate != null && errorTemplate.isError()) {
            return errorTemplate;
        }
        // Here you would typically  convert the UserRequest to a User entity, and then save to database
        if(!StringUtils.hasText(request.username())) {
            throw new UserValidationException("username", "Username cannot be empty");
        }
        if(!StringUtils.hasText(request.password())) {
            throw new UserValidationException("password", "Password cannot be empty");
        }
        if(userRepository.findByUsername(request.username()).isPresent()) {
            throw new UserValidationException("username", "Username already exists");
        }
        User user = new User();
        user = userHandlerService.convertUserRequestToUser(request, user);
        user.setStatus(Constant.ACTIVE);
        // handle user roles
        if(request.roles() != null && !request.roles().isEmpty()) {
            roleRepository.findByName(Constant.USER).ifPresent(user::addRole);
        } else {
            roleRepository.findByNameIn(request.roles()).forEach(user::addRole);
        }
        // handle user groups
        if(request.groups() != null && !request.groups().isEmpty()) {
            groupRepository.findAllByNameIn(request.groups()).forEach(user::addGroup);
        }
        // save user
        userRepository.save(user);
        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                userHandlerService.convertUserToUserResponse(user),
                false
        );
    }

    @Override
    @Transactional
    public ResponseErrorTemplate update(Long id, UserRequest request) {
        Optional<User> userOptional = userRepository.findFirstById(id);
        // Validate if user doesn't exists
        if( userOptional.isEmpty()) {
            log.error("Update user not found with id: {}", id);
            var message = String.format(ApiConstant.USER_ID_NOT_FOUND.getDescription(), id);
            return new ResponseErrorTemplate(
                    message,
                    ApiConstant.USER_ID_NOT_FOUND.getKey(),
                    new Object(),
                    true
            );
        }
        User user = userOptional.get();
        user = userHandlerService.convertUserRequestToUser(request, user);
        user.setStatus(request.status());
        userRepository.saveAndFlush(user);
        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                userHandlerService.convertUserToUserResponse(user),
                false
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        var msg = String.format(ApiConstant.USER_NAME_NOT_FOUND.getDescription(), id);
        return userOptional.map(user -> new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                userHandlerService.convertUserToUserResponse(user),
                false
        )).orElseGet(() -> new ResponseErrorTemplate(
                msg,
                ApiConstant.USER_NOT_FOUND_CODE.getKey(),
                new EmptyObject(),
                true
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findAll(UserFilterRequest filterRequest) {
        try {
            // Use userSearchService to handle the search logic and pagination
            PageableResponse<User> pageableResponse = userSearchService.searchUsers(filterRequest);
            // Handle pagination response does not return users
            if( pageableResponse == null || pageableResponse.getContent().isEmpty()) {
                log.info("No users found with filter: {}", filterRequest);
                return new ResponseErrorTemplate(
                        ApiConstant.USER_NOT_FOUND_CODE.getDescription(),
                        ApiConstant.USER_NOT_FOUND_CODE.getKey(),
                        new EmptyObject(),
                        false
                );
            }
            // Convert users to response DTOs
            List<UserResponse> userResponses = pageableResponse.getContent().stream()
                    .map(userHandlerService::convertUserToUserResponse)
                    .toList();
            // Create a pagination response with metadata
            UserPaginationResponse userPaginationResponse = new UserPaginationResponse(
                    userResponses,
                    pageableResponseHandlerService.handlePaginationResponse(
                            pageableResponse.getTotalElements(),
                            pageableResponse.getPageNumber(),
                            pageableResponse.getPageSize()
                    )
            ).withMetadata("total_users", pageableResponse.getTotalElements())
                    .withMetadata("current_page", pageableResponse.getPageNumber())
                    .withMetadata("page_size", pageableResponse.getPageSize())
                    .withMetadata("has_next", pageableResponse.hasNext())
                    .withMetadata("has_previous", pageableResponse.hasPrevious()
            );
            return new ResponseErrorTemplate(
                    ApiConstant.SUCCESS.getDescription(),
                    ApiConstant.SUCCESS.getKey(),
                    userPaginationResponse,
                    false
            );

        } catch (BusinessException e) {
            log.error("Business error retrieving users with filter {}: {}", filterRequest, e.getMessage());
            return new ResponseErrorTemplate(
                    e.getMessage(),
                    ApiConstant.BUSINESS_ERROR.getKey(),
                    new Object(),
                    true
            );
        } catch (Exception e) {
            log.error("Unexpected error retrieving users with filter {}: {}", filterRequest, e.getMessage());
            return new ResponseErrorTemplate(
                    ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey(),
                    new Object(),
                    true
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findByUsername(String username) {
        Optional<User> user = userRepository.findFirstByUsernameAndStatus(username, ApiConstant.ACTIVE.getKey());
        var msg = String.format(ApiConstant.USER_NAME_NOT_FOUND.getDescription(), username);
        return user.map(u -> new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                userHandlerService.convertUserToUserResponse(u),
                false
        )).orElseGet(() -> new ResponseErrorTemplate(
                msg,
                ApiConstant.USER_NOT_FOUND_CODE.getKey(),
                new EmptyObject(),
                true
        ));

    }

    @Override
    @Transactional
    public ResponseErrorTemplate changePassword(Long id, UserChangePasswordRequest userChangePasswordRequest) {
        try {
            // Validate request
            if (userChangePasswordRequest == null) {
                log.error("Change password request is null for user id: {}", id);
                return new ResponseErrorTemplate(
                        ApiConstant.INVALID_REQUEST.getDescription(),
                        ApiConstant.INVALID_REQUEST.getKey(),
                        new Object(),
                        true
                );
            }
            // Find user
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("User not found with id: {}", id);
                        return new BusinessException(
                                String.format(ApiConstant.USER_ID_NOT_FOUND.getDescription(), id)
                        );
                    });
            // Validate current password
            if (!passwordEncoder.matches(userChangePasswordRequest.password(), user.getPassword())) {
                log.error("Incorrect current password for user id: {}", id);
                return new ResponseErrorTemplate(
                        ApiConstant.INCORRECT_PASSWORD.getDescription(),
                        ApiConstant.INCORRECT_PASSWORD.getKey(),
                        new Object(),
                        true
                );
            }
            // Validate new password
            if (userChangePasswordRequest.newPassword().equals(userChangePasswordRequest.password())) {
                log.error("New password must be different from current password for user id: {}", id);
                return new ResponseErrorTemplate(
                        ApiConstant.NEW_PASSWORD_SAME.getDescription(),
                        ApiConstant.NEW_PASSWORD_SAME.getKey(),
                        new Object(),
                        true
                );
            }
            // Update password
            user.setPassword(passwordEncoder.encode(userChangePasswordRequest.password()));
            userRepository.save(user);
            // Log success
            log.info("Password changed successfully for user id: {}", id);

            return new ResponseErrorTemplate(
                    ApiConstant.SUCCESS.getDescription(),
                    ApiConstant.SUCCESS.getKey(),
                    new Object(),
                    false
            );
        } catch (BusinessException e) {
            log.error("Business error changing password for user id {}: {}", id, e.getMessage());
            return new ResponseErrorTemplate(
                    e.getMessage(),
                    ApiConstant.BUSINESS_ERROR.getKey(),
                    new Object(),
                    true
            );
        } catch (Exception e) {
            log.error("Error changing password for user id {}: {}", id, e.getMessage());
            return new ResponseErrorTemplate(
                    ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey(),
                    new Object(),
                    true
            );
        }
    }

    @Override
    @Transactional
    public ResponseErrorTemplate disActivateUser(Set<Long> ids, String status) {
        List<User> users = userRepository.findAllById(ids);
        // Validate if users doesn't empty
        if (users.isEmpty()) {
            log.error("No users found with ids: {}", ids);
            return new ResponseErrorTemplate(
                    ApiConstant.USER_NOT_FOUND_CODE.getDescription(),
                    ApiConstant.USER_NOT_FOUND_CODE.getKey(),
                    new EmptyObject(),
                    true
            );
        }
        users.forEach( user -> {
            user.setStatus(Optional.ofNullable(status).orElse(ApiConstant.IN_ACTIVE.getKey()));
            userRepository.saveAndFlush(user);
        });
        //refreshTokenService.deleteAllTokenByUserId(users);
        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                new EmptyObject(),
                false
        );
    }

    @Override
    @Transactional
    public ResponseErrorTemplate resetPassword(Set<Long> ids) {
        List<User> users = userRepository.findAllByIdIn(ids);
        // Validate if users doesn't empty
        if (users.isEmpty()) {
            log.info("reset user password not found with user ids: {}", ids);
            return new ResponseErrorTemplate(
                    ApiConstant.USER_NOT_FOUND_CODE.getDescription(),
                    ApiConstant.USER_NOT_FOUND_CODE.getKey(),
                    new Object(),
                    true);
        }
        users.forEach(user -> {
            user.setPassword(passwordEncoder.encode(defaultPassword));
            userRepository.saveAndFlush(user);
        });
        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                new Object(),
                false);
    }
}
