package com.parma.user.service.impl;

import com.parma.common.dto.UserRequest;
import com.parma.common.exception.ResponseErrorTemplate;
import com.parma.user.dto.request.UserChangePasswordRequest;
import com.parma.user.dto.request.UserFilterRequest;
import com.parma.user.repository.GroupRepository;
import com.parma.user.repository.RoleRepository;
import com.parma.user.repository.UserRepository;
import com.parma.user.service.UserSearchService;
import com.parma.user.service.UserService;
import com.parma.user.service.handler.PageableResponseHandlerService;
import com.parma.user.service.handler.UserHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Override
    public ResponseErrorTemplate create(UserRequest userRequest) {
        return null;
    }

    @Override
    public ResponseErrorTemplate update(Long id, UserRequest userRequest) {
        return null;
    }

    @Override
    public ResponseErrorTemplate findById(Long id) {
        return null;
    }

    @Override
    public ResponseErrorTemplate findAll(UserFilterRequest filterRequest) {
        return null;
    }

    @Override
    public ResponseErrorTemplate findByUsername(String username) {
        return null;
    }

    @Override
    public ResponseErrorTemplate changePassword(Long id, UserChangePasswordRequest userChangePasswordRequest) {
        return null;
    }

    @Override
    public ResponseErrorTemplate disActivateUser(Set<Long> ids, String status) {
        return null;
    }

    @Override
    public ResponseErrorTemplate resetPassword(Set<Long> ids) {
        return null;
    }
}
